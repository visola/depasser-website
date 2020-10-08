/**
 * Dummy Logger class. To use it, implement a new logger
 * and override the method <code>util.Logger.getLogger()</code>.
 *
 * @author Vinicius Isola
 * @requires Names
 */
Names.addClass("util.Logger", function(){
	throw new Error('Logger must not be instantiated. Use util.Logger.getLogger(options) instead.');
});

Names.depends(['util.Logger'], function () {
	
	/**
	 * Noop logger.
	 */
	var EmptyLogger = function () {};
	
	/**
	 * Log a message in this logger with the specified level.
	 *
	 * @param {Object} message The message to be logged.
	 * @param {Object} level The log entry level.
	 */
	EmptyLogger.prototype.log = function(message, level) {};
	
	/**
	 * Return a logger instance to be used.
	 *
	 * @param {Object} options	Options that will be used
	 * to create the logger instance.
	 */
	util.Logger.getLogger = function(options){
	    return new EmptyLogger();
	};
	
	/**
	 * Return a timestamp in the format: HH:MM:SS.mmm
	 *
	 * @param d {Date} 	(Optional) A date to create the timestamp from. 
	 * 					If not present it will use <code>new Date()</code>.
	 * @return {String} The timestamp.
	 */
	util.Logger.getTimestamp = function (d) {
		if (!d) d = new Date();
		var hour = d.getHours().toString();
		if (hour.length < 2) hour = '0' + hour;
		var minute = d.getMinutes().toString();
		if (minute.length < 2) minute = '0' + minute;
		var second = d.getSeconds().toString();
		if (second.length < 2) second = '0' + second;
		var millis = d.getMilliseconds().toString();
		while (millis.length < 3) millis = '0' + millis;
		return hour + ':' + minute + ':' + second + '.' + millis;
	};
});