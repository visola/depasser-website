Names.depends(['mootools.Core', 'web.XHR', 'i18n.Keys', 'JSON'], function () {
	var sendButton = $('contact').getElement('button');
	var xhr = new web.XHR({
		completed : showResponse,
		method : 'POST',
		url : ROOT + 'contact.json'
	});
	
	sendButton.addEvent('click', function () {
		var name = $$("input[name='name']").get('value');
		if (name == '') {
			alert(i18n.Keys.getValue('contact.message.allFieldsRequired'));
			return;
		}
		
		var email = $$("input[name='email']").get('value');
		if (email == '') {
			alert(i18n.Keys.getValue('contact.message.allFieldsRequired'));
			return;
		}
		
		var message = $$("textarea[name='message']").get('value');
		if (message == '') {
			alert(i18n.Keys.getValue('contact.message.allFieldsRequired'));
			return;
		}
		
		var data = {
			name : name,
			email : email,
			message : message
		}
		
		xhr.send(null, data);
		var dataElement = $('data');
		dataElement.empty();
		dataElement.grab(createLoaderImgEl());
	});
	
	function showResponse(xhr) {
		var response = JSON.parse(xhr.responseText);
		
		var dataElement = $('data');
		dataElement.empty();
		dataElement.set('html', '<p>' + response.message + '</p>');
	}
	
});