// Names - Copyright (c) 2010 Vinicius Isola, MIT style license - http://www.opensource.org/licenses/mit-license.php

/**
 * Class to control namespaces and class names. Namespaces
 * are handled like java packages. Each package is
 * represented by a javascript Object.
 */
(function () {
	if (!this.window) {
		this.window = this;
	}
	
	// Must be loaded only once
	if (window.Names) return;
	
	var Names = function () {
		throw new Error("Names: This class should not be instantiated.");
	};

	/**
	 * Stores all namespaces and classes.
	 */
	Names.names = {};

	/**
	 * Add a Class to the namespace. This is the main purpose of this
	 * implementation. Classes in javascript are just plain old functions
	 * that can be instatiated using the "new" operator. So the expected
	 * type for the "clazz" variable is function.
	 * 
	 * @param fullClassName
	 *            {String} The full name of the class that is being added,
	 *            example: "java.lang.System"
	 * @param dependsOn
	 *            {Array} (Optional) An array containing the names of classes
	 *            or namespaces that the one being created depends on.
	 * @param clazz
	 *            {Function} The class that will be stored under the desired
	 *            namespace.
	 * @return {Function} The class stored.
	 * @throws {Error}
	 *             Trying to store two classes with the same name will throw
	 *             an error.
	 * @throws {Error}
	 *             If trying to store a class with invalid type.
	 */
	Names.addClass = function () {
		var fullClassName = arguments[0];
		if (!fullClassName || fullClassName == null || fullClassName == '')
			throw new Error("Invalid class name: '" + fullClassName + "'");
		
		var clazz = null;
		var dependsOn = null;
		if (Names.isClass(arguments[1])) {
			clazz = arguments[1];
		} else {
			clazz = arguments[2];
			dependsOn = arguments[1];
			if (!Names.isClass(clazz))
				throw new Error("Invalid class type: '" + (typeof clazz) + "'");
		}
		
		Names.checkDependencies(dependsOn, fullClassName);
		
		// Separate it by dots
		var names = fullClassName.split(".");
		
		// store the class name
		var className = names[names.length - 1];

		// If the class is inside another namespace
		if (names.length > 1) {
			// This will be the namespace to store the class in
			var namespace = ""; for (var i = 0; i < names.length - 1; i++) {
				namespace += names[i];
				if (i != names.length - 2)
					namespace += ".";
			}
			
			// Create it if it doesn't exist
			var ns = Names.createNamespace(namespace);
			
			// Check to see if the class already exist
			var obj = ns[className];
			if (obj) throw new Error("Namespace is occupied: " + className);
			
			// Store the class
			ns[className] = clazz;
			
		// If class is in the default namespace
		} else {
			window[className] = clazz;
		}
		
		return clazz;
	}

	/**
	 * Check if all dependencies exists. If any of the dependencies doesn't
	 * exist, this method will throw an error.
	 * 
	 * @param dependsOn
	 *            {Array} An array with names of classes or packages to be
	 *            checked.
	 * @return Nothing.
	 */
	Names.checkDependencies = function (dependsOn, what) {
		if (dependsOn) {
			for (var i = 0; i < dependsOn.length; i++) {
				if (! Names.getNamespace(dependsOn[i])) {
					if (typeof what == 'function') {
						// Transform a function into a string with max 50 chars
						var s = what + '';
						s = s.replace(/[ \n\t\r]/ig, '');
						if (s.length > 50) s = s.substr(0, 47) + '...';
						what = s;
					}
					throw new Error('Dependency not ready: "' + what + '" depends on "' + dependsOn[i] + '"');
				}
			}
		}
	}

	/**
	 * Create a new namespace if it does not exist. If it exist, just returns
	 * it.
	 * 
	 * @param namespace
	 *            {String} A Java package like name (separated by dots).
	 *            Example: "java.util"
	 * @param dependsOn
	 *            {Array} (Optional) An array containing the names of classes
	 *            or namespaces that the one being created depends on.
	 * @return {Object} An object that represents the namespace.
	 */
	Names.createNamespace = function (newNamespace, dependsOn) {
		if (!newNamespace || newNamespace == "")
			throw Error("Can not create a namespace from empty String.");
		
		Names.checkDependencies(dependsOn, newNamespace);
		
		// Separate it by dots
		var names = newNamespace.split(".");
		
		var parent = window;
		var controlParent = Names.names;
		
		var namespace = null;
		var controlNamespace
		for (var i = 0; i < names.length; i++) {
			namespace = parent[names[i]];
			controlNamespace = controlParent[names[i]];
			
			// If the namespace doesn't exist, create it
			if (!namespace || namespace == null) {
				parent[names[i]] = controlParent[names[i]] = new Object();
				namespace = parent[names[i]];
				controlNamespace = controlParent[names[i]];
				if (i == 0) Names.names[ names[i] ] = namespace; 
			} else {
				// namespace wasn't created by this class
				if (!controlNamespace) {
					Names.names[ names[i] ] = namespace;
					controlNamespace = namespace;
				}
			}
			
			// Change the parent to the child before moving next
			parent = namespace;
			controlParent = controlNamespace
		}
		
		return namespace;
	}

	/**
	 * Execute a function if the dependencies are correctly loaded.
	 * 
	 * @param dependsOn
	 *            {Array} An array of names for namespaces (classes and
	 *            packages) that must exist before the function is executed.
	 * @param toBeExecuted
	 *            {Function} The function to be executed.
	 * @param args
	 *            {Array} An array containing all arguments to be passed to the
	 *            function.
	 * @param bindTo
	 *            {Object} Instance to bind the function execution to. The
	 *            "this" of the function.
	 * @return Whatever the function executed return.
	 */
	Names.depends = function (dependsOn, toBeExecuted, args, bindTo) {
		Names.checkDependencies(dependsOn, toBeExecuted);
		if (args == null || args.length == 0) args = [];
		if (bindTo == null) bindTo = this;
		return toBeExecuted.apply(bindTo, args);
	}

	/**
	 * Return a namespace if it exist or null if it doesn't.
	 * 
	 * @param namespace
	 *            {String} Namespace to look for.
	 * @return {Object} The namespace found or null, if not found. If the
	 *         required namespace refers to a class it will return the class
	 *         object (a function).
	 */
	Names.getNamespace = function (namespace) {
		if (!namespace || namespace == "")
			return null;
		
		// Separate it by dots
		var names = namespace.split(".");
		
		// Search for the namespace
		var name = Names.names; 
		for (var i = 0; i < names.length; i++) {
			name = name[names[i]];
			
			if (!name || name == null) {
				return null;
			}
		}
		
		return name;
	}

	/**
	 * Return a Class by its full name.
	 * 
	 * @param fullName
	 *            {String} The full class name to look for.
	 * @return {Function} The class found or null if not found.
	 */
	Names.getClass = function (fullName) {
		var clazz = Names.getNamespace(fullName);
		if (!Names.isClass(clazz)) return null;
		return clazz;
	}

	/**
	 * Iterate over all namespaces and classes in a restricted namespace or
	 * in all namespaces available.
	 * 
	 * @param callback
	 *            {Function} A callback function that will be called for
	 *            each namespace and class found. It will be passed to the
	 *            callback function the object iterated over (can be a
	 *            namespace or a class) and the namespace or class name as
	 *            String. The function may return false to stop the
	 *            iteration.
	 * @return {Object} The returned object of the execution of the callback
	 *         function for the the last time.
	 */
	Names.iterate = function (callback, namespace) {
		var ns = null;
		
		// Restrict the namespace
		if (namespace && namespace != null && namespace != '') {
			ns = Names.getNamespace(namespace);
			
		// Otherwise, return all
		} else {
			ns = Names.names; 
			namespace = null;
		}
		
		// If restricted namespace does not exist
		if (ns == null) {
			return null;
		}
		
		var result = null; for (var name in ns) {
			result = callback(ns[name], (namespace != null ? namespace + "." : "") + name);
			if (result == false) return result;
			
			if (!Names.isClass(ns[name])) {
				result = Names.iterate(callback, (namespace != null ? namespace + "." : "") + name);
				if (result == false)
					return result;
			}
		}
		
		return result;
	}

	/**
	 * Return an array with the name of all namespaces stored in this.
	 * 
	 * @param namespace
	 *            {String} Restrict the search for all the namespaces that
	 *            are inside this one.
	 * @return {Array} An array with all namespace names already created.
	 *         Null if trying to restrict the search for a non-existing
	 *         namespace.
	 */
	Names.getNamespaceNames = function (namespace) {
		var results = new Array();
		
		Names.iterate(function(item, name) {
			if ( !Names.isClass(item)) {
				results.push(name);
			}
		}, namespace);
		
		return results;
	}

	/**
	 * Return an array with the name of all classes stored in this.
	 * 
	 * @param namespace
	 *            {String} Retrict the search for classes to the specified
	 *            namespace.
	 * @return {Array} An array with all class names added to the namespace.
	 *         Null if specified a non-existing namespace.
	 */
	Names.getClassNames = function (namespace) {
		var results = new Array();
		
		Names.iterate(function(item, name) {
			if (Names.isClass(item)) {
				results.push(name);
			}
		}, namespace);
		
		return results;
	}

	/**
	 * Check if a specified object is a Class.
	 * 
	 * @param obj
	 *            {Object} The object to be checked.
	 * @return {Boolean} True if object is not null and if it is a class.
	 */
	Names.isClass = function (obj) {
		if (!obj || obj == null) return false;
		return (typeof obj) == 'function';
	}

	/**
	 * Check if a specified object is a namespace.
	 * 
	 * @param obj
	 *            {Object} The object to be checked.
	 * @return {Boolean} True if object is not null and if it is a
	 *         namespace.
	 */
	Names.isNamespace = function (obj) {
		if (!obj || obj == null) return false;
		return (typeof obj) == 'object';
	}

	window.Names = Names;
})();