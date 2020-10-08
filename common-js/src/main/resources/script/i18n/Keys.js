Names.addClass('i18n.Keys', ['web.Dependency'], function () {
	throw new Error('Must not instantiate this class.');
});

Names.depends(['i18n.Keys'], function () {
	var K = i18n.Keys;

	var keys = null;
	var values = null;
	
	function loadKeys () {
		if (keys == null) {
			var request = new web.XHR({
				async : false,
				url : ROOT + 'keys.json',
				success : function (xhr) {
					var response = JSON.parse(xhr.responseText);
					keys = response.keys;
					values = response.values;
				}
			}).send();
		}
	}
	
	K.getValue = function (key, params) {
		loadKeys();
		for (var i = 0; i < keys.length; i++) {
			if (keys[i] == key) {
				var value = values[i];
				if (params != null && params.length > 0) {
					var toReplace = value.match(/\$\d/ig);
					for (var j = 0; j < toReplace.length; j++) {
					    var index = parseInt(toReplace[j].replace("$", ''));
					    value = value.replace(toReplace[j], params[index]);
					}
				}
				return value;
			}
		}
		return '???' + key + '???' + (params?params:'');
	}
	
});