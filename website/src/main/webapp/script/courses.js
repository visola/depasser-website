Names.depends(
		[
		 	'JSON', 'mootools.Core', 'web.StateManager', 
		 	'manager.Utils', 'i18n.Keys', 'PageLoader'
		 ], 
		function () {
	
	var coursesPage = $('courses');
	var courseContent = $('courseContent');
	var leftMenu = $('leftMenu');
	var rightMenu = $('rightMenu');
	
	var ITEM_HEIGHT = 28;
	var ITEM_MARGIN = 2;
	var ITEM_PADDING = 5;
	var ONE_LINE_HEIGHT = 13;
	var ONE_LINE_PADDING_TOP = "12px";
	var ONE_LINE_PADDING_BOTTOM = "11px";

	var defaultContent = courseContent.get('html');
	var data = null;
	var hideShowEffects = {};
	var selectedCourse = null;
	
	var HIDE_ITEM = {
		'height' : 0, 
		'margin-top' : 0,
		'margin-bottom' : 0,
		'padding-bottom' : 0,
		'padding-top' : 0
	};
	
	// Set loader to left menu
	leftMenu.grab(createLoaderImgEl());
	
	var leftMenuAnimation = new Fx.Morph(leftMenu, {duration:250, unit:'px', link:'cancel'});
	var rightMenuAnimation = new Fx.Morph(rightMenu, {duration:250, unit:'px', link:'cancel'});
	
	coursesPage.addEvent('scroll', function () {
		var scroll = this.getScroll();
		if (scroll.y > 59) {
			leftMenuAnimation.start({top: scroll.y + 10});
			rightMenuAnimation.start({top: scroll.y + 10});
		} else {
			leftMenuAnimation.start({top: 69});
			rightMenuAnimation.start({top: 58});
		}
	});
	
	web.StateManager.addEvent('stateChanged', function (oldState, newState) {
		if (oldState.courseId != newState.courseId) {
			var courseId = newState.courseId;
			if (courseContent == null) return;
			loadCourse(courseId);
		}
	});
	
	var defaultEvents = {
		mouseenter: function () {
			this.addClass('hover');
		},
		mouseleave : function () {
			this.removeClass('hover');
		}
	};
	
	function closeCategory(category) {
		category = category.toUpperCase();
		var effects = hideShowEffects[category];
		for (var j = 0; j < effects.length; j++) {
			effects[j].start(HIDE_ITEM);
		}
		effects.hidden = true;
	}
	
	function createCourseElement(courseName) {
		var courseElement = new Element('li', {
			events: defaultEvents
		});
		
		var courseLink = new Element('a', {
			html : courseName.toUpperCase()
		});
		courseLink.addEvent('click', showCourseEventCallback);
		courseElement.grab(courseLink);
		
		return courseElement;
	}
	
	function createCategoryElement (category, courses) {
		var categoryListItemElement = new Element('li', {
			events : defaultEvents
		});
		
		var categoryElement = new Element('span', {
			html : category,
			events : {
				click : toggleItemsEventCallback
			}
		});
		categoryListItemElement.grab(categoryElement);
		
		var courseHandlerElement = new Element('ul');
		categoryListItemElement.grab(courseHandlerElement);
		
		// Create array to store effects for sub items
		hideShowEffects[category] = [];
		
		// For each course in this category
		for (var i = 0; i < courses.length; i++) {
			var courseElement = createCourseElement(courses[i]);
			courseHandlerElement.grab(courseElement);
			var effect = new Fx.Morph(courseElement, {
				duration: 'short',
				link : 'chain',
				unit: 'px'
			});

			// Set its dimensions based on its text length
			effect.state = {
					'height' : ITEM_HEIGHT,
					'margin-top' : ITEM_MARGIN,
					'margin-bottom' : ITEM_MARGIN,
					'padding-top' : ITEM_PADDING,
					'padding-bottom' : ITEM_PADDING
			}
			
			// Calculate how many lines will be used for this title
			var splitted = courses[i].split(' ');
			var twoLines = false;
			var counter = 0;
			for (var j = 0; j < splitted.length; j++) {
				counter += splitted[j].length + 1;
				if (counter > 20) {
					twoLines = true;
					break;
				}
			}
			
			if (!twoLines) {
				effect.state = {
						'height' : ONE_LINE_HEIGHT,
						'padding-top' : ONE_LINE_PADDING_TOP,
						'padding-bottom' : ONE_LINE_PADDING_BOTTOM
				}
			}
			
			effect.categoryElement = categoryElement;
			effect.courseElement = courseElement;
			effect.start(HIDE_ITEM);
			hideShowEffects[category].hidden = true;
			hideShowEffects[category].push(effect);
		}
		
		return categoryListItemElement;
	}
	
	function hideAll() {
		// Hide any other that is open
		for (var n in hideShowEffects) {
			if (hideShowEffects[n].hidden != true) {
				for (var j = 0; j < hideShowEffects[n].length; j++) {
					hideShowEffects[n][j].start(HIDE_ITEM);
					
				}
				hideShowEffects[n].hidden = true;
			}
		}
	}
	
	function loadCourse(courseId) {
		if (courseId == null) {
			courseContent.set('html', defaultContent);
			unselectAll();
			return;
		}
		
		courseContent.empty();
		courseContent.grab(createLoaderImgEl());
		
		web.Loader.loadURL(ROOT + 'course/load.json?courseId=' + courseId, function (resource) {
			var course = JSON.parse(resource.content);
			
			selectCourse(course.title);
			
			courseContent.empty();
			courseContent.grab(new Element('h2', {
				'html' : course.title.toUpperCase()
			}));
			
			courseContent.grab(new Element('h3', {
				html : i18n.Keys.getValue('courses.title.description')
			}));
			
			courseContent.grab(new Element('div', {
				'class' : 'description',
				'html' : course.description
			}));
			
			courseContent.grab(new Element('h3', {
				html : i18n.Keys.getValue('courses.title.content')
			}));
			
			courseContent.grab(new Element('div', {
				'class' : 'details',
				'html' : course.content
			}));
			
			courseContent.grab(new Element('h3', {
				html : i18n.Keys.getValue('courses.title.classes')
			}));
			
			if (course.classes.length != 0) {
				var nextClasses = new Element('ul');
				courseContent.grab(nextClasses);
				
				for (var i = 0; i < course.classes.length; i++) {
					var s = i18n.Keys.getValue('courses.label.schedule',
							[course.classes[i].start, course.classes[i].end, course.classes[i].schedule]);
					var classElement = new Element('li', {html:s});
					nextClasses.grab(classElement);
				}
			} else {
				courseContent.grab(new Element('p', {
					html : i18n.Keys.getValue('courses.text.noClassesAvailable')
				}));
			}
			
			// Check if scroll bar was added to the page
			var scrollSize = coursesPage.getScrollSize();
			var size = coursesPage.getSize();
			if (scrollSize.y != size.y) { // scroll bar added
				rightMenu.setStyle('right', '0');
			} else { // no scrollbar
				rightMenu.setStyle('right', '10px');
			}
		});
	}
	
	function openCategory(category) {
		category = category.toUpperCase();
		var effects = hideShowEffects[category];
		for (var j = 0; j < effects.length; j++) {
			effects[j].start(effects[j].state);
		}
		effects.hidden = false;
	}
	
	function refreshMenu() {
		leftMenu.empty();
		
		var categories = data.categories;
		for (var category in categories) {
			leftMenu.grab(createCategoryElement(category.toUpperCase(), data.categories[category]));
		}
	}
	
	function toggleCategory(category) {
		category = category.toUpperCase();
		var effects = hideShowEffects[category];
		
		if (effects.hidden) {
			openCategory(category);
		} else {
			closeCategory(category);
		}
	}
	
	function selectCourse(course) {
		course = course.toUpperCase();
		if (course == selectedCourse) {
			return;
		}
		unselectAll();
		
		for (var category in hideShowEffects) {
			var categoryEffects = hideShowEffects[category];
			var shouldOpenCategory = false;
			
			// Look for effects for the specified course
			for (var i = 0; i < categoryEffects.length; i++) {
				if (categoryEffects[i].courseElement.getElement('a').get('html') == course) {
					categoryEffects[i].courseElement.addClass('selected');
					categoryEffects[i].courseElement.getParent().getParent().addClass('selected');
					shouldOpenCategory = true;
				}
			}
			
			if (shouldOpenCategory) openCategory(category);
		}
		
		selectedCourse = course;
	}
	
	function showCourseEventCallback() {
		var title = this.get('html');
		
		this.addClass('selected');
		
		for (var i = 0; i < data.titles.length; i++) {
			if (data.titles[i].toUpperCase() == title) {
				web.StateManager.setValue('courseId', data.ids[i]);
				return;
			}
		}
	}
	
	function toggleItemsEventCallback () {
		var category = this.get('html');
		toggleCategory(category);
	}
	
	function unselectAll() {
		selectedCourse = null;
		leftMenu.getElements('li').each(function (item) {
			item.removeClass('selected');
		});
	}
	
	// Load courses titles
	web.Loader.loadURL(ROOT + 'course/all.json', function (resource) {
		var result = JSON.parse(resource.content);
		data = result;
		refreshMenu();
		
		var courseId = web.StateManager.getState().courseId;
		if (courseId != null) {
			// In case this is a direct URL to a course content
			loadCourse(courseId);
		}
	});
});