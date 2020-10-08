/**
 * Class that represents actions to be executed. Actions can have a
 * list of dependencies that are other actions that must be executed before it.
 * This can create a very complex dependency tree that will be resolved
 * automatically as each action that doesn't have any pending dependencies are
 * being executed.
 * 
 * @param type
 *            {String} A type identifier for this action.
 * @param execute
 *            {Function} The function to be executed. It will be binded to the
 *            Action instance.
 * @constructor
 */
Names.addClass('util.Action', ['util.Logger'], function (type, toExecute) {
	util.Action.instanceCounter++;
	
	this.id = util.Action.instanceCounter;
	
	if (typeof type != 'string') throw new Error('Actions need a type.');
	this.type = type;
	this.created = new Date();
	this.logger = util.Logger.getLogger({name : 'Action ' + this.toString()});
	this.callback = toExecute;
	this.dependencies = [];
	this.logger.log('Action created: ' + this.toString());
});

Names.depends(['util.Action'], function () {
	var Action = util.Action;
	
	/**
	 * Store types defined by other classes.
	 */
	Action.Types = {};
	
	/**
	 * Counts how many action instances there are.
	 */
	Action.instanceCounter = 0;
	
	/**
	 * Add a dependency to this action.
	 * 
	 * @param action
	 *            {Action} Another action that this depends
	 *            on.
	 */
	Action.prototype.addDependency = function (action) {
		if (!action) return;
		if (action.id == this.id) {
			throw new Error("Can not add an action as a dependency to itself.");
		}
		this.logger.log('Adding dependency: ' + this.id + ' -> ' + action.id);
		this.dependencies.push(action);
	};
	
	/**
	 * Add a list of dependencies to this action.
	 * 
	 * @param actions
	 *            {Array} An array with other actions that
	 *            this depends on.
	 */
	Action.prototype.addDependencies = function (actions) {
		for (var i = 0; i < actions.length; i++) {
			this.addDependency(actions[i]);
		}
	};
	
	/**
	 * Execute this action. Any arguments passed to this
	 * function will be passed to the callback function.
	 */
	Action.prototype.execute = function () {
		this.logger.log('Executing action: ' + this.id + ' (' + this.type + ')');
		this.started = new Date();
		if (this.callback) {
			this.callback.apply(this, arguments);
		} else {
			throw new Error('No callback defined for action: ' + this.id);
		}
	};

	/**
	 * Mark this action as finished and remove it from the
	 * list of actions to be executed.
	 */
	Action.prototype.finish = function () {
		this.finished = new Date();
		this.executor.removeAction(this.id);
		this.logger.log('Action ' + this.id + ' finished in ' + (this.finished.getTime() - this.started.getTime()) + 'ms');
	};

	/**
	 * Check if this action has pending dependencies. This
	 * function is recursive and will check if all the
	 * dependency tree for this action (dependendcies of
	 * dependencies) are ready.
	 * 
	 * @return {Boolean} True if all dependencies are ready.
	 */
	Action.prototype.hasDependency = function () {
		for (var i = 0; i < this.dependencies.length; i++) {
			if (!this.dependencies[i].finished || this.dependencies[i].hasDependency()) {
				return true;
			}
		}
		return false;
	};
	
	Action.prototype.toString = function () {
		return this.id + ' (' + this.type + ')';
	}
});