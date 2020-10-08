/**
 * Generic slot management.
 */
Names.depends(['mootools.Core'], function () {
	// Creates the class
	Names.addClass('util.SlotManager', new Class({
		/**
		 * Store all slots.
		 * 
		 * @private
		 */
		allSlots : [],
		
		/**
		 * Store all free slots.
		 * 
		 * @private
		 */
		freeSlots : [],
		
		/**
		 * Default options.
		 */
		options : {
			countX : 5,
			countY : 5
		},
		
		Implements : [Options],
		
		/**
		 * Constructor.
		 * 
		 * @param {Object} options	Used to create the new object.
		 */
		initialize : function (options) {
			this.setOptions(options);
			
			var pos = null;
			var counter = 0;
			for (var i = 0; i < this.options.countX; i++) {
				for (var j = 0; j < this.options.countY; j++) {
					pos = {};
					pos.index = counter;
					pos.x = i;
					pos.y = j;
					this.freeSlots.push(pos);
					this.allSlots.push(pos);
					counter++;
				}
			}
		},
		
		getCount : function () {
			return this.options.countX * this.options.countY;
		},
		
		popFreeSlot : function () {
			var count = this.freeSlots.length - 1;
			var toPop = Math.round(count * Math.random()); // between 0 and count
			var pos = this.freeSlots[toPop];
			this.freeSlots.splice(toPop, 1);
			return pos;
		}
		
	}));
});