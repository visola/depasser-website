/**
 * <p>
 * State manager is the ajax history control. It manages the
 * state of the actual page based on the hash part of the URL.
 * </p>
 * 
 * <p>
 * This is a static class, should not be instantiated.
 * </p>
 * 
 * <p>
 * Based on HistoryManager (http://digitarald.de/project/history-manager/) 
 * and Momkai Address (http://www.momkai.com/)
 * </p>
 *  
 * @author Vinicius Isola (viniciusisola@gmail.com)
 */
Names.depends(['util.EventManager', 'JSON'], function () {
	
	Names.addClass('web.StateManager', function () {
		throw new Error('Must not instantiate this class.');
	});
	
	/**
	 * Track if the state should be checked.
	 * 
	 * @private
	 */
	var keepChecking = true;
	
	/**
	 * Delay that will be used to check for the page state.
	 * 
	 * @private
	 */
	var delay = 100;
	
	/**
	 * Logger for this class.
	 */
	var logger = util.Logger.getLogger({name:'web.StateManager'});
	
	/**
	 * Page state as it was last time checked.
	 * 
	 * @private
	 */
	var state = {};
	
	/**
	 * Id of the last timer set.
	 * 
	 * @private
	 */
	var timerId = -1;
	
	/**
	 * Object that handles events.
	 */
	var events = new util.EventManager();
	
	/**
	 * Get the state object from the page URL.
	 * 
	 * @private
	 * @return {Object} The actual page state.
	 */
	var getPageState = function () {
		var hash = window.location.hash;
		if (hash != '') hash = hash.substr(1);
		else return {};
		
		hash = unescape(hash);
		var state = JSON.parse(hash);
		return state;
	};
	

	/**
	 * Change the page state to a whole new state, removing all old states
	 * set before.
	 * 
	 * @param stateObject
	 *            {Object} An object that will be serialized to the hash.
	 */
	web.StateManager.setState = function (stateObject) {
		var newHash = JSON.stringify(stateObject);
		window.location.hash = '#' + newHash;
	}

	/**
	 * Set a value to a property in the page state. If the property doesn't
	 * exist it will be created. If it is different than the existing one,
	 * it will trigger the state changed event.
	 * 
	 * @param propName
	 *            {String} Name of the property that is changing.
	 * @param propValue
	 *            {String} Value to be set to.
	 */
	web.StateManager.setValue = function(propName, propValue) {
		var actualState = web.StateManager.getState();
		var oldValue = actualState[propName];
		if (oldValue === undefined) oldValue = null;
		
		// Page will change
		if (propValue !== oldValue) {
			if (propValue == null) {
				delete actualState[propName];
			} else {
				actualState[propName] = propValue;
			}
			web.StateManager.setState(actualState);
		}
	}
		
	/**
	 * Check the state of this page and if it changed,
	 * fire the event 'stateChanged'.
	 * 
	 * @private
	 */
	checkState = function () {
		var oldState = state;
		var newState = web.StateManager.getState();
		
		// Count the number of properties in each state
		var oldProps = [];
		for (var n in oldState) {
			oldProps.push(n);
		}
		oldProps.sort();
		
		var newProps = [];
		for (var n in newState) {
			newProps.push(n);
		}
		newProps.sort();
		
		// State changed, number of properties is different
		if (newProps.length != oldProps.length) {
			state = newState;
			events.fireEvent('stateChanged', [oldState, newState]);
		} else {
			// Check each property
			for (var i = 0; i < oldProps.length; i++) {
				if (oldProps[i] != newProps[i] || oldState[oldProps[i]] != newState[newProps[i]]) {
					state = newState;
					events.fireEvent('stateChanged', [oldState, newState]);
					break;
				}
			}
		}
		
		// Rerun this after delay
		timerId = setTimeout(checkState, delay);
	}
		
	/**
	 * Return the actual state of this page. Changes to this object
	 * will not affect the state of the page.
	 * 
	 * @return {Object} 	An object representing the state of the page.
	 */
	web.StateManager.getState = function () {
		return getPageState();
	}
	
	/**
	 * Pause this manager from checking state changes.
	 */
	web.StateManager.pause = function () {
		logger.log('Pausing state manager...');
		if (timerId != -1) {
			clearTimeout(timerId);
			timerId = -1;
		}
	}
		
	/**
	 * Start to listen the state of the page.
	 */
	web.StateManager.start = function () {
		logger.log('Running state manager...');
		checkState();
	}
	
	/**
	 * Delegate event methods.
	 */
	for (var n in events) {
		if (typeof events[n] == 'function') web.StateManager[n] = events[n].bind(events);
	}
	
	web.StateManager.start();
});