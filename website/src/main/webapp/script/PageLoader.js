Names.depends(['util.Logger', 'web.Loader', 'JSON', 'web.Dependency'], function () {
	Names.createNamespace('PageLoader');
	
	var logger = util.Logger.getLogger({name:'PageLoader'});
	
	var pageLoaded = null;
	var styles = [];
	var loadCallbacks = [];
	var unloadCallbacks = [];
	
	PageLoader.addLoadCallback = function (callback) {
		if (callback) {
			logger.log('Adding page loaded callback.');
			loadCallbacks.push(callback);
		}
	}
	
	PageLoader.addUnloadCallback = function (callback) {
		if (callback) {
			logger.log('Adding page unloaded callback.');
			unloadCallbacks.push(callback);
		}
	}
	
	PageLoader.pageReady = function () {
		for (var i = loadCallbacks.length - 1; i >= 0; i--) {
			logger.log('Calling page loaded: ' + i);
			var callback = loadCallbacks.pop();
			callback();
		}
	}
	
	PageLoader.loadContent = function (id) {
		logger.log('Loading content: ' + id);
		pageLoaded = id;
	}
	
	PageLoader.loadTemplate = function (name, pageElement) {
		logger.log('Loading template: ' + name);
		web.Loader.loadURL(ROOT + 'template/html.json?name=' + name, function (resource) {
			var template = JSON.parse(resource.content).template;
			pageElement.innerHTML = template.html;
			pageLoaded = pageElement;
			
			// Load and execute scripts
			for (var i = 0; i < template.scripts.length; i++) {
				web.Dependency.loadScript('script/' + template.scripts[i], true);
			}
			
			// Load styles
			var headElement = $$('head');
			var files = "";
			for (var i = 0; i < template.styles.length; i++) {
				files += 'file=style/' + escape(template.styles[i]) + "&";
			}
			files = files.substr(0, files.length - 1);
			
			var styleEl = new Element('link', {
				rel : 'stylesheet',
				type : 'text/css',
				href : ROOT + "resources.do?" + files 
			});
			
			// Push style element to the stack
			styles.push(styleEl);
			headElement.grab(styleEl);
		});
	}
	
	PageLoader.unloadContent = function () {
		logger.log('Unloading content...');
	}
	
	PageLoader.unloadTemplate = function () {
		logger.log('Unloading template...');
		if (pageLoaded) {
			pageLoaded.empty();
		}
		for (var i = 0; i < styles.length; i++) {
			styles[i].dispose();
		}
		for (var i = unloadCallbacks.length - 1; i >= 0; i--) {
			var callback = unloadCallbacks.pop();
			callback();
		}
		pageLoaded = null;
	}
	
});