// EventManager - Copyright (c) 2010 Vinicius Isola, MIT style license - http://www.opensource.org/licenses/mit-license.php
// Based on MooTools - Events

/**
 * <p>
 * Event manager class. Used to control listeners and 
 * fire events when necessary, calling the listeners
 * and passing arguments to them.
 * </p>
 * 
 * @param owner {String} The name of the owner for this
 * 			manager. It is used for logging purposes.
 * @author Vinicius Isola
 */
Names.addClass('util.EventManager', ['util.Logger'], function (owner) {
	if (owner) {
		this.owner = owner;
		this.logger = util.Logger.getLogger({name: owner + ':EventManager'});
	} else {
		this.logger = util.Logger.getLogger({name: 'EventManager'});
	}
	this.listeners = {};
});

Names.depends(['util.EventManager'], function () {
	/**
	 * <p>
	 * Add a callback to be executed when a event is fired.
	 * </p>
	 * 
	 * @param eventName {String} 	The name of the event that
	 * 								the callback should wait for.
	 * @param callback {Function}	Callback to be executed.
	 */
	util.EventManager.prototype.addEvent = function (eventName, callback) {
		this.logger.log('Adding event: ' + eventName);
		if (typeof eventName != 'string') throw new Error('EventManager.addEvent: eventName must be a string.');
		if (typeof callback != 'function') throw new Error('EventManager.addEvent: callback must be a function.');
		var ls = this.listeners[eventName];
		if (!ls) {
			ls = [];
			this.listeners[eventName] = ls;
		}
		
		// Check if this callback was already added to this manager
		var sCallback = callback + '';
		for (var i = 0; i < ls.length; i++) {
			if (ls[i] + '' == sCallback) {
				// Remove it, so it can be added again
				this.logger.log('Removing old callback...');
				ls.splice(i, 1);
				break;
			}
		}
		
		ls.push(callback);
	};
	
	util.EventManager.prototype.addEvents = function (events) {
		this.logger.log('Adding events...');
		if (typeof events != 'object') throw new Error('EventManager.addEvents: argument must be an object.');
		for (var n in events) {
			this.addEvent(n, events[n]);
		}
	};
	
	util.EventManager.prototype.fireEvent = function (event, args, bindTo) {
		this.logger.log('Firing event: ' + event);
		var ls = this.listeners[event];
		if (!ls) return; // no listeners
		if (!bindTo) bindTo = this; // If no one to bind to
		for (var i = 0; i < ls.length; i++) {
			ls[i].apply(bindTo, args);
		}
	};
	
	util.EventManager.prototype.removeEvent = function (eventName, callback) {
		this.logger.log('Removing event: ' + eventName + ', ' + callback);
		if (typeof eventName != 'string') throw new Error('EventManager.removeEvent: eventName must be a string.');
		if (typeof callback != 'function') throw new Error('EventManager.removeEvent: callback must be a function.');
		var ls = this.listeners[eventName];
		if (ls) {
			for (var i = 0; i < ls.length; i++) {
				if (ls[i] == callback) {
					ls.splice(i, 1);
					break;
				}
			}
		}
	};
	
	util.EventManager.prototype.removeEvents = function (eventName) {
		this.logger.log('Removing events: ' + eventName);
		if (typeof eventName != 'string') throw new Error('EventManager.removeEvents: eventName must be a string.');
		if (! this.listeners[eventName]) return;
		this.listeners[eventName] = [];
	};
});