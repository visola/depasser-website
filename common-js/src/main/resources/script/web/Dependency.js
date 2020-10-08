// Dependency - Copyright (c) 2010 Vinicius Isola, MIT style license - http://www.opensource.org/licenses/mit-license.php

// Don't load this class twice
if ( ! Names.getNamespace('web.Dependency') ) {
	/**
	 * <p>
	 * This class load resources and control dependencies. It's a static class 
	 * and should not be instantiated.
	 * </p>
	 * 
	 * @author Vinicius Isola
	 */
	Names.createNamespace('web.Dependency', 
			['util.Logger', 'util.EventManager', 'util.Action', 
			 'util.Executor', 'web.Loader']);
	
	Names.depends(['web.Dependency'], function () {
		if (!this.window) {
			this.window = this;
		}
		
		var Dependency = web.Dependency;
		
		//------ Private Constants ------
		/**
		 * Action type, Add Class Action.
		 */
		util.Action.Types.ADD_CLASS = 'ADD_CLASS';
		
		/**
		 * Action type, Execute Action.
		 */
		util.Action.Types.EXECUTE = 'EXECUTE';
	
		/**
		 * Action type, Execute Later Action.
		 */
		util.Action.Types.EXEC_LATER = 'EXECUTE LATER';
		
		//------ Private Data ------
		var eventManager = new util.EventManager('web.Dependency');
		
		var executor = new util.Executor();
		
		/**
		 * Logger for this namespace.
		 */
		var logger = util.Logger.getLogger({name:'web.Dependency'});
	
		//------ Augment Names class to enable async dependency loading ------
		
		// Store old add class, to be used when needed
		Dependency.addClass = Names.addClass;
	
		/**
		 * Replace Names.addClass to avoid creating classes
		 * without their dependencies.
		 */
		Names.addClass = function () {
			var fullClassName = arguments[0];
			if (!fullClassName || fullClassName == null || fullClassName == '')
				throw new Error("Invalid class name: '" + fullClassName + "'");
			
			logger.log('Adding class: ' + fullClassName);
			
			var clazz = null;
			var dependsOn = [];
			if (Names.isClass(arguments[1])) {
				clazz = arguments[1];
			} else {
				clazz = arguments[2];
				dependsOn = arguments[1];
				if (!Names.isClass(clazz)) {
					throw new Error("Invalid class type: '" + (typeof clazz) + "'");
				}
			}
			
			// If there are no dependencies, execute it now
			if (dependsOn.length == 0) {
				Dependency.addClass(fullClassName, clazz);
			} else {
				// Create a new action, to add the desired class
				var action = new util.Action(util.Action.Types.ADD_CLASS, function () {
					Dependency.addClass(this.className, clazz);
					this.finish();
				});
				
				// Set the class name to be added
				action.className = fullClassName;
				
				// Add dependencies to the function to be executed
				for (var i = 0; i < dependsOn.length; i++) {
					var a = Dependency.importClass(dependsOn[i]);
					if (a != null) {
						action.addDependency(a);
					}
				}
				
				// Add dependencies
				action.addDependencies(executor.getExecutingDependencies());
				executor.execute(action);
			}
		};
		
		// Store old depends function to be used when needed
		Dependency.depends = Names.depends;
		
		/**
		 * Replaces Names.depends to avoid executing content without
		 * their dependencies.
		 */
		Names.depends = function (dependsOn, callback) {
			logger.log('Adding function to execute later.');
			var executeAction = new util.Action(util.Action.Types.EXEC_LATER, function () {
				callback();
				this.finish();
			});
			
			executeAction.addDependencies(executor.getExecutingDependencies());
			
			// Add dependencies to the function to be executed
			for (var i = 0; i < dependsOn.length; i++) {
				var a = Dependency.importClass(dependsOn[i]);
				if (a != null) {
					executeAction.addDependency(a);
				}
			}
			
			executor.execute(executeAction);
			return executeAction;
		};
		
		//------ Private methods ------
		/**
		 * Action method to execute a script. This should be binded
		 * to an Action instance.
		 */
		var executeScript = function () {
			logger.log('Executing script: ' + this.url);
			
			var resource = web.Loader.getResource(this.url);
			executor.setExecuting(this, resource);
			
			try {
				var toExecute = new Function(resource.content);
				this.result = toExecute();
			} catch (e) {
				this.logger.log('Error while executing resource: ' + this.url);
				throw e;
			}
			
			executor.popExecuting();
			this.finish();
		};
		
		/**
		 * Add a file to the "to load" queue and start the loading thread.
		 */
		var loadFile = function () {
			web.Loader.load(this);
		};
		
	    //------ Public Methods ------
		/**
		 * <p>
		 * Load a javascript class file and execute it, waiting for the dependencies
		 * if any. A javascript class should be identified as: util.Names. That will
		 * be translated to a URL using two variables if available: window.ROOT and 
		 * Dependency.SCRIPT. Example: <code>ROOT = '/ContentManager'</code> and
		 * <code>Dependency.SCRIPT = 'script'</code>, the class <code>util.Names</code>
		 * will be translated to the path: <code>'/ContentManager/script/util/Names.js'</code>.
		 * </p>
		 * 
		 * <p>
		 * This won't block. If your class depends on the class that is being imported, use
		 * <code>Names.addClass</code> to create it. <code>Dependency</code> will replace that
		 * function to guarantee that your class will be executed only when all the imports
		 * are ready.
		 * </p>
		 * 
		 * <p>
		 * If the same class is imported twice, <code>Dependency</code> will also guarantee that
		 * it won't be executed twice. If it is already loaded and ready to use, the import
		 * will be ignored.
		 * </p>
		 * 
		 * @param className {String} 	The full name for the class.
		 * @return	{Action} 	The execute action that depends on the load action. These two
		 * 						actions will be used to load an execute the file where the class
		 * 						should be in. After both finishes the class should be ready to use.
		 * 						If the class was already loaded, null will be returned. 
		 */
		Dependency.importClass = function (className) {
			// Class may be declared in the same file, and may be waiting to be executed 
			for (var i = 0; i < executor.actions.length; i++) {
				if (executor.actions[i].className && executor.actions[i].className == className) {
					return executor.actions[i];
				}
			}
			
			// If already defined, don't load it again
			if (Names.getNamespace(className) != null) return null;
			
			logger.log('Class requested: ' + className);
			var url = web.Loader.getClassURL(className);
			var executeAction = Dependency.loadScript(url);		
			return executeAction;
		};
		
		Dependency.loadFile = function (fileToLoad) {
			logger.log('Loading file: ' + fileToLoad);
			for (var i = 0; i < executor.actions.length; i++) {
				if (executor.actions[i].url && executor.actions[i].url == fileToLoad) {
					return null;
				}
			}
			
			var loadAction = new util.Action(util.Action.Types.LOAD, loadFile);
			loadAction.url = fileToLoad;
			executor.execute(loadAction);
			return loadAction;
		}
		
		Dependency.loadScript = function (scriptToLoad, reexecute) {
			logger.log('Loading script: ' + scriptToLoad);
			var loadAction = Dependency.loadFile(scriptToLoad);
			if (loadAction == null && !reexecute) return null;
			
			var executeAction = new util.Action(util.Action.Types.EXECUTE, executeScript);
			executeAction.addDependency(loadAction);
			executeAction.url = scriptToLoad;
			executor.execute(executeAction);
			return executeAction;
		}
	});
}