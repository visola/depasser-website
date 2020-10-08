Names.addClass("util.Logger.ConsoleLogger", ['util.Logger'], function(options){
	if (options && options.name) {
		this.loggerName = options.name;
	}
});

Names.depends(['util.Logger', 'util.Logger.ConsoleLogger'], function () {
	util.Logger.getLogger = function(options){
	    return new util.Logger.ConsoleLogger(options);
	};
	
	var ignore = [];
	
	util.Logger.ConsoleLogger.ignore = function (loggerName) {
		ignore.push(loggerName); 
	};
	
	if (window.console && window.console.log) {
		util.Logger.ConsoleLogger.log = function (loggerName, userMessage) {
			if (!loggerName) loggerName = 'ROOT';
			for (var i = 0; i < ignore.length; i++) {
				if (loggerName.indexOf(ignore[i]) == 0) return;
			}
			var message = '';
			message += '[' + util.Logger.getTimestamp() + '] ';
			if (loggerName) message += loggerName + ' - ';
			message += userMessage
			window.console.log(message);		
		};
	} else {
		alert('Console not available.');
		util.Logger.ConsoleLogger.log = function () {};
	}
	
	util.Logger.ConsoleLogger.prototype.log = function(userMessage) {
		util.Logger.ConsoleLogger.log(this.loggerName, userMessage);
	};
});