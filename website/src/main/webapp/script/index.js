Names.depends(['mootools.Core', 'util.SlotManager', 'web.StateManager', 'PageLoader'], function () {
	var Effects = {};
	
	var NUMBER_OF_SCREENS = {
		x : 5,
		y : 5
	};
	
	var slotManager = new util.SlotManager({
		countX : NUMBER_OF_SCREENS.x,
		countY : NUMBER_OF_SCREENS.y
	});
	
	var CONFIG = Names.createNamespace('CONFIG');
	CONFIG.EFFECT = {
		DURATION : 2000
	};
	
	var moving = [];
	
	var pages = [];
	
	var elements = {
		initialize : function () {
			this.body = $$('body')[0];
			this.container = $('container');
			this.footer = $('footer');
			this.header = $('header');
			this.universe = $('universe');
			this.logo = $$('#header > img');
			this.logoSmall = $$('#header > img.small');
		},
		setSizes : function () {
			var bodySize = this.body.getSize();
			var headerSize = this.header.getSize();
			var footerSize = this.footer.getSize();
			var containerSize = this.container.getSize();
			
			this.header.setStyle('left', ((bodySize.x - headerSize.x) / 2) + 'px');
			this.footer.setStyle('left', ((bodySize.x - footerSize.x) / 2) + 'px');
			
			// This is used as the view port
			this.container.setStyles({
				height : bodySize.y + 'px',
				left : '0px',
				top : '0px',
				width : bodySize.x + 'px'
			});
			
			// This is the container for all pages
			this.universe.setStyles({
				position : 'absolute',
				height : (NUMBER_OF_SCREENS.y * bodySize.y) + 'px',
				width : (NUMBER_OF_SCREENS.x * bodySize.x) + 'px',
			});
		}
	}
	
	function createPageElement(linkElement, index) {
		var pos = slotManager.popFreeSlot();
		
		var oldHref = linkElement.get('href');
		// remove root context
		if (oldHref.indexOf(ROOT) == 0) {
			oldHref = oldHref.substring(ROOT.length, oldHref.length);
		}
		
		linkElement.set('href', null);
		linkElement.addEvent('click', function (event) {
			web.StateManager.setState({'page' : pos.href});
			event.preventDefault();
		});
		
		var splitted = oldHref.split('/');
		var pageId = splitted[splitted.length - 1];
		
		var pageElement = $(document.createElement('div'));
		pageElement.set({
			id : pageId,
			'class' : 'page',
			styles : {
				position : 'absolute'
			}
		});
		pageElement.linkElement = linkElement;
		pageElement.pos = pos;
		pageElement.pos.href = oldHref;
		return pageElement;
	}
	
	function resize () {
		elements.setSizes();
		var bodySize = elements.body.getSize();
		var footerPosition = elements.footer.getPosition();
		var footerSize = elements.footer.getSize();
		var headerPosition = elements.header.getPosition();
		var headerSize = elements.header.getSize();
		var universeSize = elements.universe.getSize();
		
		// Set the size and position for each page
		for (var i = 0; i < pages.length; i++) {
			var pos = pages[i].pos;
			// Get padding
			var padTop = parseInt(pages[i].getStyle('padding-top'), 10);
			var padBot = parseInt(pages[i].getStyle('padding-bottom'), 10);
			var padLeft = parseInt(pages[i].getStyle('padding-left'), 10);
			var padRight = parseInt(pages[i].getStyle('padding-right'), 10);
			
			pages[i].setStyles({
				height : (bodySize.y - footerSize.y - headerSize.y - padTop - padBot) + 'px',
				left : (headerPosition.x + (pos.x * bodySize.x)) + 'px',
				top : (headerPosition.y + headerSize.y + (pos.y * bodySize.y)) + 'px',
				width : (headerSize.x - padLeft - padRight) + 'px',
			});
		}
		
		window.goToPage(web.StateManager.getState().page);
	}
	
	window.createLoaderImgEl = function () {
		return new Element("img", {src : ROOT + 'img/ajax-loader.gif'});
	}
	
	window.goToPage = function (href) {
		if (!Effects.MoveHeader) return;
		
		var page = null;
		for (var i = 0; i < pages.length; i++) {
			if (pages[i].pos.href == href) {
				page = pages[i];
				break;
			}
		}
		
		if (page == null) {
			page = pages[0];
		}
		
		(function () {
			if (page.pos.href == pages[0].pos.href) {
				elements.logo.setStyle('display', 'inline');
				elements.logoSmall.setStyle('display', 'none');
			} else {
				elements.logo.setStyle('display', 'none');
				elements.logoSmall.setStyle('display', 'inline');
			}
		}).delay(CONFIG.EFFECT.DURATION / 2);
		
		PageLoader.unloadTemplate();
		PageLoader.loadTemplate(page.pos.href, page);
		
		var parent = page.linkElement.getParent();
		parent.getSiblings().each(function (el) {
			el.removeClass('selected');
		});
		parent.addClass('selected');
		
		var bodySize = elements.body.getSize();
		
		Effects.MoveHeader.start({opacity : 0});
		Effects.MoveHeader.start.delay(Math.round(3 * CONFIG.EFFECT.DURATION / 4), Effects.MoveHeader, {opacity : 1});
		
		var footer = elements.footer;
		Effects.MoveFooter.start({opacity: 0});
		Effects.MoveFooter.start.delay(Math.round(3 * CONFIG.EFFECT.DURATION / 4), Effects.MoveFooter, {opacity : 1});
		
		Effects.MoveUniverse.start.delay(Math.round(CONFIG.EFFECT.DURATION / 4), Effects.MoveUniverse, {
			left : - (bodySize.x * page.pos.x),
			top : - (bodySize.y * page.pos.y)
		});
	}
	
	$(window).addEvents({
		'domready': function () {
			// Add small logo for other pages
			var smallLogo = new Element('img', {
				src : ROOT  + 'img/logo_pequeno.png',
				alt : 'Logo pequeno',
				'class' : 'small'
			});
			
			$('header').grab(smallLogo, 'top');
			
			web.StateManager.addEvent('stateChanged', function (oldState, newState) {
				if (oldState.page != newState.page) {
					window.goToPage(newState.page);
				}
			});
			
			elements.initialize();
			
			var universe = elements.universe;
			var bodySize = elements.body.getSize();
			var universeSize = universe.getSize();
			
			// Add arrows behind
			for (var i = 0; i < 10; i++) {
				for (var j = 0; j < 10; j++) {
					elements.universe.grab(new Element('img', {
						src : ROOT + 'img/setaG.png',
						styles : {
							left : (i * bodySize.x + (bodySize.x - 380)) + 'px',
							opacity: '0.5',
							position : 'absolute',
							top : (j * Math.round(bodySize.y / 1.7) - 100) + 'px'
						}
					}));
				}
			}
			
			// Add dots behind
			for (var i = 0; i < 5; i++) {
				elements.universe.grab(new Element('div', {
					styles : {
						background : "url('" + ROOT + "img/bolinhas.png')",
						height: '300px',
						left : '0',
						position : 'absolute',
						top : (i * bodySize.y + 350) + 'px',
						width: '50000px'
					}
				}));
			}
			
			var links = $('menu').getElements('li a');
			for (var i = 0; i < links.length; i++) {
				var pageElement = createPageElement(links[i], i);
				pages.push(pageElement);
				universe.grab(pageElement);
			}
			resize();
		
			Effects.MoveUniverse = new Fx.Morph(universe,{
				duration : Math.round(CONFIG.EFFECT.DURATION / 2),
				link : 'cancel',
				unit : 'px',
				onComplete : PageLoader.pageReady
			});
			
			Effects.MoveHeader = new Fx.Morph(elements.header, {
				duration : Math.round(CONFIG.EFFECT.DURATION / 4),
				link : 'cancel'
			});
			
			Effects.MoveFooter = new Fx.Morph(elements.footer, {
				duration : Math.round(CONFIG.EFFECT.DURATION / 4),
				link : 'cancel'
			});
			
			var state = web.StateManager.getState();
			if (state.page) {
				window.goToPage(state.page);
			} else {
				window.goToPage(pages[0].pos.href);
			}
		},
		'resize' : resize
	});	
});