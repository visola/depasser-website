Names.addClass('manager.Utils', function () {
	throw new Error('Must not instantiate this class.');
});

Names.depends(['manager.Utils'], function () {
	var u = manager.Utils;
	
	u.getFieldValue = function (content, typeName) {
		for (var i = 0; i < content.fields.length; i++) {
			if (content.fields[i].type.name == typeName) {
				return content.fields[i].value;
			}
		}
		return undefined;
	}
});