/**
 * A loader controls the resources that were loaded and avoid doing the same
 * request twice. It also uses a proxy to load resources, when possible, to
 * reduce the number of requests done.
 */
Names.createNamespace('web.Loader', ['JSON', 'util.EventManager', 'util.Action', 'util.Executor', 'util.Logger', 'web.XHR']);

Names.depends(['web.Loader'], function () {
	
	var Loader = web.Loader;
	
	//------ Private variables ------
	var logger = util.Logger.getLogger({name:'web.Loader'});
	
	var eventManager = new util.EventManager('web.Loader');
	
	//------ Callbacks ------
	/**
	 * Callback for failed requests.
	 */
	var loadingFailed = function (xhr, url) {
		throw new Error('Error while loading resource: ' + url);
	};
	
	var resourceLoaded = function (resource) {
		if (resource == null) return null;
		
		logger.log('Resource loaded: ' + resource.url);
		Loader.loaded.push(resource);
		
		// Check if there is a callback to call for this resource
		for (var i = Loader.callbacks.length - 1; i >= 0; i--) {
			if (Loader.callbacks[i].url == resource.url) {
				Loader.callbacks[i].callback(resource); // call the callback
				Loader.callbacks.splice(i, 1); // remove it from the array
			}
		}
		
		/* Finish action associated with it and remove it from the
		 * toLoad list.
		 */
		for (var i = Loader.toLoad.length - 1; i >= 0; i--) {
			if (Loader.toLoad[i].loading && Loader.toLoad[i].url == resource.url) {
				Loader.toLoad[i].finish();
				Loader.toLoad.splice(i, 1);
				break;
			}
		}
		
		eventManager.fireEvent('resourceLoaded', resource);
	};
	
	/**
	 * Callback for proxied resources.
	 */
	var proxiedResourcesLoaded = function(resources) {
		// Response will be an array of resources
		for (var i = 0; i < resources.length; i++) {
			resourceLoaded({
				content : resources[i].content,
				loaded : new Date(),
				url : resources[i].path
			});
		}
	};
	
	//------ Public properties ------
	/**
	 * Action type, Load Action.
	 */
	util.Action.Types.LOAD = 'LOAD';
	
	/**
	 * Callbacks to execute for each resource.
	 */
	Loader.callbacks = [];
	
	/**
	 * Executors that this loader should care about.
	 */
	Loader.executors = [];
	
	/**
	 * Resources that were loaded.
	 */
	Loader.loaded = [];
	
	/**
	 * Actions that are waiting to be loaded.
	 */
	Loader.toLoad = [];
	
	/**
	 * Resource proxy that will be used to load all resources. It will be sent a
	 * list of files in the <code>file</code> parameter and the return must be
	 * an array containing all data, formatted as an object with two meaningful
	 * properties: content and path. Content is the content loaded and path the
	 * path requested that resulted in the requested resource.
	 */
	Loader.PROXY = 'resources.json';
	
	/**
	 * This will be added in front of all class names to be loaded.
	 */
	Loader.scriptDir = 'script';
	
	//------ Public functions ------
	/**
	 * Add an executor to this loader to check for other load actions.
	 */
	Loader.addExecutor = function (executor) {
		Loader.executors.push(executor);
	}

	/**
	 * Load a resource directly, without proxying it. This
	 * will make a direct call to the url passed in. Use
	 * this to load dynamic pages or content that can only
	 * be accessed by its URL.
	 * 
	 * @param url
	 *            {String} Path to the resource to be
	 *            loaded.
	 * @param data
	 *            {Object} Data to be sent to server, using
	 *            a post request.
	 * @param callback
	 *            {Function} Callback to be called when the
	 *            resource is loaded.
	 */
	Loader.loadURL = function (url, userData, callback) {
		var resource = Loader.getResource(url);
		
		// If second parameter is an object
		var data = null;
		if (typeof userData == 'function') {
			callback = userData
		} else {
			data = userData;
		}
		
		// If resource was loaded previously
		if (resource != null) {
			// Call the callback immediately, if defined
			if (callback) callback(resource);
			return;
		}
		
		if (callback) {
			Loader.callbacks.push({
				callback : callback,
				url : url
			});
		}
		
		// Load the resource
		var request = new web.XHR({
			method : (data == null ? 'GET' : 'POST'), 
			failed : loadingFailed,
			success : function (xhr, url) {
				resourceLoaded({
					content : xhr.responseText,
					data: data,
					loaded : new Date(),
					url : url
				});
			},
			url : url
		});
		request.send(null, data);
	}

	/**
	 * Add an action that points to a resource to be loaded.
	 * 
	 * @param path
	 *            {util.Action|String} The action object
	 *            with an <code>url</code> property that
	 *            points to the path, or a <code>String</code>
	 *            that represents a URL to be loaded.
	 */
	Loader.load = function (path) {
		var action = null;
		
		if (path instanceof util.Action) {
			action = path;
		} else {
			action = new util.Action(util.Action.Types.LOAD, function () {});
			action.url = path;
		}
		
		for (var i = 0; i < Loader.loaded.length; i++) {
			// If resource was loaded
			if (Loader.loaded[i].url == action.url) {
				action.content = Loader.loaded[i].content;
				action.finish();
				return;
			}
		}
		
		// Add resource to be loaded
		Loader.toLoad.push(action);
		Loader.start();
	}
	
	/**
	 * Execute this loader. Will check all executors associated
	 * with the loader to see if there are other load actions
	 * to be executed. It there is, it will them to reduce the
	 * number of requests executed. 
	 */
	Loader.start = function () {
		logger.log('Executing load...');
		
		if (Loader.timerId) {
			logger.log('Clearing timeout: ' + Loader.timerId);
			clearTimeout(Loader.timerId);
		}
		
		// Check all actions to see if there is any other loading action not running
		for (var i = 0; i < Loader.executors.length; i++) {
			var e = Loader.executors[i];
			for (var j = 0; j < e.actions.length; j++) {
				if (e.actions[j].type == util.Action.Types.LOAD && e.actions[j].started == null) {
					logger.log('There are load actions to execute, waiting to load resources...');
					Loader.timerId = setTimeout(Loader.start, 0);
					return;
				}
			}
		}
		
		var url = Loader.PROXY;
		url += '?';
		for (var i = 0; i < Loader.toLoad.length; i++) {
			if (Loader.toLoad[i].loading != true) {
				url += 'file=' + Loader.toLoad[i].url + '&';
				Loader.toLoad[i].loading = true;
			}
		}
		
		url = url.substr(0, url.length -1);
		
		var request = new web.XHR({
			failed : loadingFailed,
			success : function (xhr, url) {
				// Response is a JSON object
				var answer = JSON.parse(xhr.responseText);
				proxiedResourcesLoaded(answer);
			},
			url : url
		});
		request.send();
	}
	
	/**
	 * <p>
	 * Transform a class name into an URL to be loaded.
	 * <code>Dependency.SCRIPT</code> will be added to all
	 * paths.
	 * </p>
	 * 
	 * @param className
	 *            {String} Class name to be transformed.
	 * @return {String} An path to the class name.
	 */
	Loader.getClassURL = function (className) {
		var url = web.Loader.scriptDir + '/' + className.replace(/\./g, '/');
		url += '.js';
		
		return url;
	};
	

	/**
	 * Retrieve a resource previously loaded.
	 * 
	 * @param url
	 *            {String} Path that identify the resource.
	 * @param data
	 *            {Object} An object representing the data
	 *            sent in the body of the request.
	 * @return {String} The content loaded or available, or
	 *         null if not found.
	 */
	Loader.getResource = function (url, data) {
		for (var i = 0; i < Loader.loaded.length; i++) {
			if (Loader.loaded[i].url == url) {
				var resource = Loader.loaded[i]; 
				// If same URL, check for data
				if (data == null && resource.data != null) continue;
				if (data != null && resource.data == null) continue;
				
				// If data is available
				var equals = true;
				for (var n in data) {
					if (data[n] != resource.data[n]) {
						equals = false;
						break;
					}
				}
				
				if (equals) {
					return Loader.loaded[i];
				}
			}
		}
		return null;
	};
	
	// Delegate event methods
	for (var n in eventManager) {
		if (typeof eventManager[n] == 'function') {
			Loader[n] = function () {
				eventManager[n].apply(eventManager, arguments);
			}
		}
	}
	
});