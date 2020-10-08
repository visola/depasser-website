Names.addClass('util.Executor', ['util.Action', 'util.Logger'], function () {
	/** 
	 * Actions to be processed.
	 */
	this.actions = [];
	
	this.total = 0;
	
	/**
	 * Executing stack.
	 */
	this.executing = [];
	
	/**
	 * This instance logger.
	 */
	this.logger = util.Logger.getLogger({name: 'util.Executor'});
	
	// Add this executor to web.Loader if exist
	if (Names.getNamespace('web.Loader')) web.Loader.addExecutor(this);
});

Names.depends(['util.Executor'], function () {
	
	var Executor = util.Executor;

	/**
	 * Add an action to be executed and set this executor to run some time
	 * in the future.
	 * 
	 * @param action
	 *            {util.Action} The action to be executed.
	 */
	Executor.prototype.execute = function (action) {
		if (!action || ! (action instanceof util.Action)) {
			throw new Error('Invalid action: ' + action);
		}
		
		// Add dependency to all executing actions
		for (var i = 0; i < this.executing.length; i++) {
			this.executing[i].action.addDependency(action);
		}
		
		this.actions.push(action);
		this.setStart();
	}
	

	/**
	 * Return all dependencies from all actions that were pushed into the
	 * <code>executing</code> stack.
	 * 
	 * @return {Array} An array with all dependencies.
	 */
	Executor.prototype.getExecutingDependencies = function () {
		var dependencies = [];
		for (var i = 0; i < this.executing.length; i++) {
			for (var j = 0; j < this.executing[i].dependencies.length; j++) {
				dependencies.push(this.executing[i].action.dependencies[j]);
			}
		}
		return dependencies;
	}
	
	/**
	 * Pop the last action added as executing from the stack.
	 */
	Executor.prototype.popExecuting = function () {
		this.executing.pop();
	}

	/**
	 * Remove an action added to this executor.
	 * 
	 * @param actionId
	 *            {Number} Id of the action to be removed.
	 */
	Executor.prototype.removeAction = function (actionId) {
		// Find the action and remove it from the array
		for (var i = 0; i < this.actions.length; i++) {
			if (this.actions[i].id == actionId) {
				if (this.actions[i].started && this.actions[i].finished) {
					this.total += this.actions[i].finished.getTime() - this.actions[i].started.getTime();
				}
				this.actions.splice(i, 1);
				break;
			}
		}
	}

	/**
	 * Set an action (and associated resource) as executing.
	 * 
	 * @param action
	 *            {util.Action} The action that is executing now.
	 * @param resource
	 *            {Object} (Optional) The resource associated with the
	 *            action, if any.
	 */
	Executor.prototype.setExecuting = function (action, resource) {
		this.executing.push({
				action : action,
				dependencies : [],
				resource : resource
		});
	}
	
	/**
	 * Set this executor to execute again some time in the future.
	 */
	Executor.prototype.setStart = function () {
		if (this.timerId) clearTimeout(this.timerId);
		var self = this;
		this.timerId = setTimeout(function () {
			self.start();
		}, 0);
	}

	Executor.prototype.start = function () {
		if (this.actions.length == 0) return;
		var error = null;
		
		// Run actions backwards, dependencies works faster that way
		for (var i = this.actions.length - 1; i >= 0 ; i--) {
			var action = this.actions[i];
			if (action && !action.hasDependency() && !action.started) {
				this.setExecuting(action);
				try {
					action.executor = this;
					action.execute();
				} catch (e) {
					this.logger.log('::Error:: executing action ' + action.toString());
					action.finish();
					this.actions = [];
					error = e;
				}
				this.popExecuting();
			}
		}
		
		this.logger.log('Actions to execute: ' + this.actions.length);
		if (this.actions.length > 0) {
			this.setStart();
		} else {
			this.logger.log('Time running: ' + this.total + 'ms');
			this.timerId = null;
		}
		if (error) {
			throw error;
		}
	}
	
});