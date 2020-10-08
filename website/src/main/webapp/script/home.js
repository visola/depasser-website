Names.depends(['mootools.Core', 'web.XHR', 'JSON'], function () {
	var bannerIndex, bannersEl, bannerWidth, buttonsElement, counter, timerId;
	var bannerEffects = [];
	
	var classesHolder = $('blue').getElement('div');
	var homePageEl = $('home');
	var postsHolder = $('posts').getElement('div'); 
	
	// Add arrows element
	homePageEl.grab(new Element('img', {
		id : 'arrows',
		src : ROOT + 'img/setaG.png' 
	}), 'top');
	
	var texts = [
	             ["TURMAS COM", "NO MÁXIMO", "<span>6</span> ALUNOS"],
	             ["<span>PROJETOS</span> PARA", "PRATICAR O CONTEÚDO", "APRENDIDO"],
	             ["AULAS <span>LIVRES</span>", "COM O PROFESSOR", "PARA PRATICAR"]
	             ];
	
	var createBannerElement = function (text, imgName) {
		var bannerEl = new Element('div', {
			"class" : "banner"
		});
		
		bannerEl.grab(new Element('img', {
			src : imgName
		}));
		
		var textEl = new Element('div');
		bannerEl.grab(textEl);
		
		for (var i = 0; i < text.length; i++) {
			textEl.grab(new Element('span', {
				"class" : 'banner_text_' + (i + 1),
				html : text[i]
			}));
		}
		
		return bannerEl;
	}
	
	var createClassElement = function (courseClass) {
		var classElement = new Element('p');
		if (courseClass.start.length > 10) {
			courseClass.start = courseClass.start.substring(0, 10); 
		}
		
		var date = new Date(
				courseClass.start.substring(0,4), 
				parseInt(courseClass.start.substring(5,7)) - 1, 
				courseClass.start.substring(8, 10));
		var sDate = date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear();
		classElement.grab(new Element('span', {html:sDate}));
		
		var courseLink = new Element('a', {
			html : courseClass.title
		});
		classElement.grab(courseLink);
		
		courseLink.addEvent('click', function () {
			web.StateManager.setState({
				"page": "courses",
				"courseId" : courseClass.id
			});
		});
		
		return classElement;
	}
	
	var createIndexElement = function (index) {
		var element = new Element('span', {
			html : (index + 1)
		});
		element.addEvent('click', function () {
			changeBanner(index);
		});
		return element;
	}
	
	var createPostElement = function (post) {
		var postElement = new Element('div', {
			'class' : 'postElement'
		});
		
		var date = new Date(post.date.substring(0,4), 
				parseInt(post.date.substring(5,7)) - 1, 
				post.date.substring(8, 10));
		var sDate = date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear();
		postElement.grab(new Element('p', {
			'class' : 'date',
			html : sDate
		}));
		
		var postTitle = new Element('h2');
		postElement.grab(postTitle);
		
		var postLink = new Element('a', {
			html : post.title
		});
		postTitle.grab(postLink);
		postLink.addEvent('click', function () {
			web.StateManager.setState({
				page : 'blog',
				postId : post.id
			});
		});
		
		// Filter abstract for first paragraph only
		var splitted = post['abstract'].split(/<\/{0,1}p[^>]*>/);
		var paragraph = null;
		for (var i = 0; i < splitted.length; i++) {
			if (splitted[i].indexOf('<') == -1 && splitted[i].length > 0) {
			    paragraph = splitted[i];
			    break;
			}
		}
		
		if (paragraph.length > 250) {
			paragraph = paragraph.substring(0, 247) + '...'; 
		}
		postElement.grab(new Element('p', {
			html : paragraph
		}));
		
		return postElement;
	}

	bannersEl = new Element('div', {
		id : 'banners'
	});
	homePageEl.grab(bannersEl);
	
	// element that contains the banner buttons
	buttonsElement = new Element('div', {
		id : "buttons"
	});
	
	// Add banner elements
	counter = 1;
	for (var i = 0; i < texts.length; i++) {
		var bannerEl = createBannerElement(texts[i], ROOT + 'img/home/banners00' + counter + '.png');
		bannersEl.grab(bannerEl);
		
		buttonsElement.grab(createIndexElement(i));
		
		// Increment counter
		counter++;
		if (counter > 5) counter = 1;
		
		// Create an effect object for this banner
		var effect = new Fx.Morph(bannerEl, {
			duration : 500,
			link: 'cancel'
		});
		bannerEffects.push(effect);
		
		// Store banner size
		if (!bannerWidth) bannerWidth = bannerEl.getSize().x;
	}
	
	// Add buttons at the end, so they stand on front of everything
	bannersEl.grab(buttonsElement);
	
	bannerIndex = bannerEffects.length - 1;
	var changeBanner = function (goToIndex) {
		if (goToIndex != null) {
			// If same banner, don't execute
			if (goToIndex == bannerIndex) {
				setChangeBanner(5);
				return;
			}
			// If out of range
			if (goToIndex < 0 || goToIndex > bannerEffects.length) {
				goToIndex = 0;
			}
		} 
		
		
		clearTimeout(timerId);
		var thisBanner = bannerEffects[bannerIndex];
		
		var nextIndex = -1;
		if (goToIndex != null) {
			nextIndex = goToIndex;
		} else {
			nextIndex = bannerIndex + 1;
			if (nextIndex == bannerEffects.length) nextIndex = 0;
		}

		var nextBanner = bannerEffects[nextIndex];
		
		// hide all others
		for (var i = 0; i < bannerEffects.length; i++) {
			if (i != nextIndex) {
				bannerEffects[i].start({opacity:0});
			}
		}
		
		// show selected banner
		nextBanner.start.delay(250, nextBanner, {opacity:1});
		
		// set selected button
		var buttons = buttonsElement.getElements('span');
		for (var i = 0; i < buttons.length; i++) {
			buttons[i].removeClass('selected');
		}
		buttons[nextIndex].addClass('selected');
		
		bannerIndex = nextIndex;
		
		if (goToIndex != null) {
			setChangeBanner(15);
		} else {
			setChangeBanner(5);
		}
	}
	
	var loadCourseClasses = function () {
		classesHolder.empty();
		classesHolder.grab(createLoaderImgEl());
		new web.XHR({
			url : ROOT + 'course/next.json',
			success : function (xhr) {
				var classes = JSON.parse(xhr.responseText).nextClasses;
				classesHolder.empty();
				var max = classes.length;
				if (max > 5) max = 5;
				for (var i = 0; i < max; i++) {
					classesHolder.grab(createClassElement(classes[i]));
				}
			}
		}).send();
	}
	
	var loadLatestPosts = function () {
		postsHolder.empty();
		postsHolder.grab(createLoaderImgEl());
		new web.XHR({
			url : ROOT + 'blog/posts.json',
			success : function (xhr) {
				postsHolder.empty();
				var posts = JSON.parse(xhr.responseText).content;
				var postsElements = new Element('div');
				postsHolder.grab(postsElements);
				
				if (posts.length == 0) {
					postsElements.grab(new Element('div', {
						'class' : 'postElement',
						html : '<p>Nenhum post disponível.</p>'
					}));
				} else {
					for (var i = 0; i < posts.length; i++) {
						postsElements.grab(createPostElement(posts[i]));
					}
					
					var fx = new Fx.Morph(postsElements, {link : 'cancel', unit: 'px', duration: 500});
					var postHeight = postsElements.getElement('div').getStyle('height').toInt(); 
					var maxHeight = - postHeight * (posts.length - 1);
					var nextHeight = 0;
					function nextPost () {
						nextHeight -= postHeight;
						if (nextHeight < maxHeight) nextHeight = 0;
						fx.start({'top' : nextHeight});
						setTimeout(nextPost, 3000);
					}
					setTimeout(nextPost, 3000);
				}
			}
		}).send();
	}
	
	function setChangeBanner(delay) {
		timerId = setTimeout(function () {changeBanner();}, delay * 1000);
	}
	
	changeBanner();
	loadCourseClasses();
	loadLatestPosts();
	
	PageLoader.addUnloadCallback(function () {
		clearTimeout(timerId);
		bannersEl.dispose();
	});
});