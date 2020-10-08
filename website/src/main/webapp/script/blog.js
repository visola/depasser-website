Names.depends(['mootools.Core', 'web.XHR', 'web.Loader', 'web.StateManager', 'i18n.Keys'], function () {
	var categories = null;
	var dates = [];
	var recent = [];
	var tags = null;
	
	var TAG_COLORS = ['#c8c8c8', '#61c7f4', '#5e5e5e', '#00b1ff'];
	var MONTH_NAMES = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 
	             'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];
	
	var leftMenu = $('leftMenu');
	var rightMenu = $('rightMenu');
	
	var categoryList = leftMenu.getElement('ul');
	var dateList = rightMenu.getElement('ul');
	var page = $('blog');
	var posts = $('blogPosts');
	var tagCloud = leftMenu.getElement('p');
	
	web.StateManager.addEvent('stateChanged', function (oldState, newState) {
		setPageState(newState);
	});
	
	var leftMenuAnimation = new Fx.Morph(leftMenu, {duration:250, unit:'px', link:'cancel'});
	var rightMenuAnimation = new Fx.Morph(rightMenu, {duration:250, unit:'px', link:'cancel'});
	
	page.addEvent('scroll', function () {
		var scroll = this.getScroll();
		if (scroll.y > 59) {
			leftMenuAnimation.start({top: scroll.y + 10});
			rightMenuAnimation.start({top: scroll.y + 10});
		} else {
			leftMenuAnimation.start({top: 69});
			rightMenuAnimation.start({top: 69});
		}
	});
	
	// Set the loader gif
	categoryList.grab(new Element('li').grab(createLoaderImgEl()));
	
	tagCloud.grab(createLoaderImgEl());
	
	dateList.grab(new Element('li').grab(createLoaderImgEl()));
	
	function createCategoryElement(category, count) {
		var result = new Element('li');
		
		var link = new Element('a', {
			html : category.toUpperCase() + ' (' + count + ')'
		});
		result.grab(link);
		link.addEvent('click', function () {
			setCategory(category);
		});
		
		return result;
	}
	
	function createCommentForm (post) {
		var formElement = new Element('form');
		
		formElement.grab(new Element('input', {
			name : 'postId',
			type : 'hidden',
			value : post.id 
		}));
		
		formElement.grab(new Element('input', {
			name : 'postLanguage',
			type : 'hidden',
			value : post.language
		}));
		
		formElement.grab(new Element('label', {
			'for' : 'name',
			'html' : i18n.Keys.getValue('blog.comments.form.label.name')
		}));
		
		formElement.grab(new Element('input', {
			name: 'name'
		}));
		
		formElement.grab(new Element('br'));
		
		formElement.grab(new Element('label',{
			'for':'comment',
			'html' : i18n.Keys.getValue('blog.comments.form.label.comment')
		}));
		
		formElement.grab(new Element('br'));
		
		formElement.grab(new Element('textarea', {
			name : 'comment'
		}));
		
		formElement.grab(new Element('input', {
			type : 'submit',
			value : i18n.Keys.getValue('blog.comments.form.label.submit')
		}));
		
		formElement.addEvent('submit', submitCommentFormEvent);
		
		return formElement;
	}
	
	function createFilterObject(stateObject) {
		var filterObject = {};
		
		if (stateObject.year) filterObject.year = stateObject.year;
		if (stateObject.month) filterObject.month = stateObject.month;
		if (stateObject.category) filterObject.category = stateObject.category;
		if (stateObject.tag) filterObject.tag = stateObject.tag;
		if (stateObject.postId) filterObject.postId = stateObject.postId;
		
		return filterObject;
	}
	
	function createFullBlogPostElement(post) {
		var postElement = new Element('div', {
			'class' : 'blogPost'
		});
		
		postElement.grab(new Element('h2', {
			html : post.title
		}));
		
		postElement.grab(new Element('div', {
			'class' : 'blogContent',
			html : post.content
		}));
		
		var commentsTitle = new Element('h3', {
			html : '&nbsp;' + i18n.Keys.getValue('blog.title.comments', [post.comments.length]) 
		});
		postElement.grab(commentsTitle);
		commentsTitle.grab(new Element('img', {
			src : ROOT + 'img/blog/balao.png'
		}), 'top');
		
		
		postElement.grab(createCommentForm(post));
		
		for (var i = 0; i < post.comments.length; i++) {
			var commentEl = new Element('div', {'class' : 'postComment'});
			postElement.grab(commentEl);
			
			commentEl.grab(new Element('h4', {
				html : i18n.Keys.getValue('blog.title.by', [post.comments[i].by, post.comments[i].date])
			}));
			
			commentEl.grab(new Element('p', {
				html : post.comments[i].content
			}));
		}
		
		return postElement;
	}
	
	function createMonthElement(year, month, count) {
		var monthEl = new Element('li');
		
		var monthLink = new Element('a', {
			html : '- ' + MONTH_NAMES[month].toUpperCase() + ' (' + count + ')'
		});
		monthEl.grab(monthLink);
		
		monthLink.addEvent('click', function () {
			setMonth(year, month);
		});
		
		return monthEl;
	}
	
	function createPostElement(post) {
		var postElement = new Element('div', {
			'class' : 'blogPost'
		});
		
		var postTitle = new Element('h2');
		postElement.grab(postTitle);
		
		var postTitleLink = new Element('a', {
			html : post.title
		});
		postTitle.grab(postTitleLink);
		postTitleLink.addEvent('click', function () {
			clearAll();
			web.StateManager.setValue('postId', post.id);
		});
		
		postElement.grab(new Element('p', {
			html : post.date + ', por ' + post.by
		}));
		
		postElement.grab(new Element('div', {
			'class' : 'abstract',
			html : post["abstract"]
		}));
		
		var commentHolder = new Element('div', {
			'class' : 'comment'
		});
		commentHolder.grab(new Element('img', {
			src : ROOT + 'img/blog/balao.png'
		}));
		commentHolder.appendText('Comentários (' + post.comments + ')');
		postElement.grab(commentHolder);
		
		return postElement;
	}
	
	function createTagElement(tag, count, max) {
		var colorIndex = parseInt((count / max) * (TAG_COLORS.length - 1));
		var tagEl = new Element('a', {
			html : tag + ' ',
			styles : {
				color : TAG_COLORS[colorIndex],
				fontSize : parseInt(10 + 10 * (count / max)) + 'pt'
			}
		});
		
		tagEl.addEvent('click', function () {
			setTag(tag);
		});
		
		return tagEl;
	}
	
	function createYearElement(year, months) {
		var yearEl = new Element('li');
		
		var yearText = new Element('a');
		yearEl.grab(yearText);
		yearText.addEvent('click', function () {
			setYear(year);
		});
		
		var monthList = new Element('ul');
		yearEl.grab(monthList);
		
		var count = 0;
		
		for (var i = 0; i < months.length; i++) {
			var monthCount = dates[year][months[i]];
			count += monthCount;
			var monthEl = createMonthElement(year, months[i], monthCount);
			monthList.grab(monthEl);
		}
		
		yearText.set('html', '> ' + year + ' (' + count + ')');
		
		return yearEl;
	}
	
	function loadPosts(filterObject) {
		posts.empty();
		posts.grab(createLoaderImgEl());
		
		if (filterObject == null) {
			web.Loader.loadURL(ROOT + 'blog/posts.json', loadPostsCallback);
		} else {
			web.Loader.loadURL(ROOT + 'blog/posts.json', filterObject, loadPostsCallback);
		}
	}
	
	function loadPostsCallback(resource) {
		var result = JSON.parse(resource.content);
		recent = result.content;
		refreshPosts();
	}
	
	function refreshCategories() {
		categoryList.empty();
		
		for (var categoryName in categories) {
			var categoryEl = createCategoryElement(categoryName, categories[categoryName]);
			categoryList.grab(categoryEl);
		}
	}
	
	function refreshDates() {
		dateList.empty();
		
		// Guarantee that the years will be backwards
		var years = [];
		for (var year in dates) {
			years.push(year);
		}
		years.sort(sortNumberDescending);

		for (var i = 0; i < years.length; i++) {
			var months = [];
			for (var month in dates[years[i]]) {
				months.push(month);
			}
			months.sort(sortNumberDescending);
			
			var yearEl = createYearElement(years[i], months);
			dateList.grab(yearEl);
		}
	}
	
	function refreshPosts() {
		posts.empty();
		
		if (recent.length == 0) {
			posts.grab(new Element('p', {html : 'Nenhum post encontrado.'}));
		} else {
			for (var i = 0; i < recent.length; i++) {
				var postElement = createPostElement(recent[i]);
				posts.grab(postElement);
			}
		}
	}
	
	function refreshTags() {
		tagCloud.empty();
		
		var max = 0;
		for (var tag in tags) {
			if (tags[tag] > max) {
				max = tags[tag];
			}
		}
		
		for (var tag in tags) {
			var tagElement = createTagElement(tag, tags[tag], max);
			tagCloud.grab(tagElement);
		}
	}
	
	function clearAll() {
		web.StateManager.setValue('month', null);
		web.StateManager.setValue('year', null);
		web.StateManager.setValue('category', null);
		web.StateManager.setValue('tag', null);
		web.StateManager.setValue('postId', null);
	}
	
	function setCategory(category) {
		clearAll();
		web.StateManager.setValue('category', category);
	}
	
	function setMonth(year, month) {
		clearAll();
		web.StateManager.setValue('month', month);
		web.StateManager.setValue('year', year);
	}
	
	function setPageState(newState) {
		leftMenuAnimation.set({top: 69});
		rightMenuAnimation.set({top: 69});
		if (newState.postId != null) {
			showPost(newState.postId);
		} else {
			loadPosts(createFilterObject(newState));
		}
	}
	
	function setTag(tag) {
		clearAll();
		web.StateManager.setValue('tag', tag);
	}
	
	function setYear(year) {
		clearAll();
		web.StateManager.setValue('year', year);
	}
	
	function showPost(postId) {
		posts.grab(createLoaderImgEl());
		new web.XHR({
			url : ROOT + 'blog/load.json?id=' + postId,
			success : function (xhr) {
				var post = JSON.parse(xhr.responseText);
				posts.empty();
				posts.grab(createFullBlogPostElement(post));
			} 
		}).send();
	}
	
	function sortNumberDescending(n1, n2) {
		return n2 - n1;
	}
	
	function submitCommentFormEvent (event) {
		var nameInput = this.getElement("input[name='name']");
		var commentText = this.getElement('textarea');
		
		var name = nameInput.get('value'); 
		if (name == null || name == '') {
			alert(i18n.Keys.getValue('blog.comments.message.emptyName'));
			return false;
		}
		
		var comment = commentText.get('value');
		if (comment == null || comment == '') {
			alert(i18n.Keys.getValue('blog.comments.message.emptyComment'));
			return false;
		}
		
		var postId = this.getElement("input[name='postId']").get('value');
		var postLanguage = this.getElement("input[name='postLanguage']").get('value');
		
		var data = {
			commentText : comment,
			name : name,
			postId : postId,
			postLanguage : postLanguage
		};
		
		var loader = new Element('span');
		loader.grab(createLoaderImgEl());
		loader.appendText(i18n.Keys.getValue('message.loading') + '...');
		loader.replaces(this);
		
		new web.XHR({
			data : data,
			url : ROOT + 'blog/comment.json',
			method : 'POST',
			completed : function (xhr) {
				var messageEl = new Element('span', {
					html : '<strong>' + JSON.parse(xhr.responseText).message + '</strong>' 
				});
				messageEl.replaces(loader);
			}
		}).send();
		
		return false;
	}
	
	web.Loader.loadURL(ROOT + 'blog/categories.json', function (resource) {
		var result = JSON.parse(resource.content);
		categories = result.categories;
		refreshCategories();
	});
	
	web.Loader.loadURL(ROOT + 'blog/dates.json', function (resource) {
		var result = JSON.parse(resource.content);
		dates = result.dates;
		refreshDates();
	});
	
	web.Loader.loadURL(ROOT + 'blog/tags.json', function (resource) {
		var result = JSON.parse(resource.content);
		tags = result.tags;
		refreshTags();
	});
	
	setPageState(web.StateManager.getState());
});