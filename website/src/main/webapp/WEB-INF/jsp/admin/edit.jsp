<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="r" uri="http://www.depasser.com.br/web/resources" %>
<html>
	<head>
		<title>Content</title>
		<r:resources>
			script/Names.js
			script/JSON.js
			script/mootools/Core.js
		</r:resources>
		<style>
			#contentList {
				border: 1px solid black;
				height: 350px;
				list-style: none;
				margin: 0;
				overflow: auto;
				padding: 0;
				width: 250px;
			}
			
			#contentList li {
				border: 1px solid black;
				height: 50px;
				margin: 1px;
				padding: 5px;
				position: relative;
			}
			
			#contentList li.selected {
				border: 2px inset grey;
			}
			
			#contentList li span {
				font-size: 0.7em;
				position: absolute;
			}
			
			#contentList li span.template {
				bottom: 2px;
				font-size: 10pt;
				left: 2px;
			}
			
			#contentList li span.title {
				font-size: 12pt;
				left: 2px;
				top: 20px;
			}
			
			.id {
				top: 2px;
				left: 2px;
			}
			
			.language {
				top: 2px;
				right: 2px;
			}
			
			.template_1 {
				background: rgb(200, 255, 200);
			}
			
			.template_2 {
				background: rgb(255, 255, 200);
			}
			
			.template_3 {
				background: rgb(255, 200, 200);
			}
			
			.template_4 {
				background: rgb(200, 200, 255);
			}
		</style>
		<script>
			var templates = [],
			contentList = [];
			
			function buildTemplateForm(templateId) {
				clearForm();
				
				var template = getTemplate(templateId);
				if (template == null) return;
				
				$('templateId').set('value', template.id);
				
				var types = template.types;
				
				var dynamicForm = $('dynamicForm');
				
				for (var i = 0; i < types.length; i++) {
					var type = types[i];
					dynamicForm.grab(new Element('input', {
						name : 'typeIds',
						value : type.id,
						type : 'hidden'
					}));
					
					dynamicForm.grab(getFieldElement(type, i));
					
					dynamicForm.grab(new Element('br'));
				}
				
				$('content').setStyle('display', 'block');
			}
			
			function clearForm() {
				$('contentId').set('value', null);
				$('templateId').set('value', null);
				$('title').set('value', null);
				$('language').set('value', null);
				$('dynamicForm').empty();
				$('content').setStyle('display', 'none');
			}
			
			function compareCourse(c1, c2) {
				if (c1.template > c2.template) return 1;
				if (c1.template < c2.template) return -1;
				
				if (c1.title > c2.title) return 1;
				if (c1.title < c2.title) return -1;
				
				if (c1.id > c2.id) return 1;
				if (c1.id < c2.id) return -1;
				
				return 0;
			}
			
			function createContentElement(content) {
				var contentElement = new Element('li', {
					id : content.id
				});
				
				contentElement.addEvent('click', function () {
					fillContentData(content.id);
					this.getParent().getChildren('li').each(function (item) {
						item.removeClass('selected');
					});
					this.addClass('selected');
				});
				
				contentElement.addClass('template_' + content.templateId);
				
				contentElement.grab(new Element('span', {
					'class' : 'id',
					'html' : content.id
				}));
				
				contentElement.grab(new Element('span', {
					'class' : 'language',
					'html' : content.language
				}));
				
				contentElement.grab(new Element('span', {
					'class' : 'title',
					'html' : content.title
				}));
				
				contentElement.grab(new Element('span', {
					'class' : 'template',
					'html' : content.template
				}));
				
				return contentElement;
			}
			
			function fillContentData(contentId) {
				clearForm();
				
				var content = getLocalContent(contentId);
				content = getContent(content.id, content.language);
				var template = getTemplate(content.template.id);
				
				$('contentId').set('value', contentId);
				$('language').set('value', content.language);
				$('title').set('value', content.title);
				
				$('templateId').set('value', content.template.id);
				
				var fields = content.fields;
				var dynamicForm = $('dynamicForm');
				for (var i = 0; i < fields.length; i++) {
					var type = fields[i].type;
					dynamicForm.grab(new Element('input', {
						name : 'typeIds',
						value : type.id,
						type : 'hidden'
					}));
					
					dynamicForm.grab(getFieldElement(type, i, fields[i]));
					dynamicForm.grab(new Element('br'));
				}
				
				$('content').setStyle('display', 'block');
			}
			
			function getFieldElement(type, index, field) {
				var result = new Element('span');
				result.grab(new Element('label', {
					'for' : 'values',
					'html' : type.name + ':'
				}));
				
				switch (type.value) {
				case 'br.com.depasser.content.field.ParentField':
					var input = new Element('input',{
						name : 'values'
					});
					result.grab(input);
					if (field != null) {
						input.set('value', field.value.id + ', ' + field.value.language);
					}
					break;
				case 'br.com.depasser.content.field.LongTextField':
					result.grab(new Element('br'));
					
					var textElement = new Element('textarea', {
						name: 'values',
						styles: {
							height: '250px',
							width: '350px'
						}
					});
					result.grab(textElement);
					
					if (field != null) {
						textElement.set('html', field.value);	
					}
					break;
				case 'br.com.depasser.content.field.MultiValueField':
					var input = new Element('input',{
						name : 'values'
					});
					result.grab(input);
					
					if (field != null) {
						var value = '';
						for (var i = 0; i < field.values.length; i++) {
							value += field.values[i].value;
							value += ', ';
						}
						input.set('value', value);
					}
					
					if (type.options.length > 0) {
						var options = new Element('span');
						result.grab(options);
						for (var i = 0; i < type.options.length; i++) {
							options.appendText(type.options[i].value + ', ');
						}
					}
					
					break;
				default:
					var input = new Element('input',{
						name : 'values'
					});
					result.grab(input);
					if (field != null) {
						input.set('value', field.value);
					}
					break;
				}
				
				return result;
			}
			
			function getContent(id, language) {
				var c = null;
				new Request({
					async : false,
					data: {id:id, language:language},
					url : 'content.json',
					onSuccess : function (data) {
						c = JSON.parse(data).content;
					}
				}).send();
				
				return c;
			}
			
			function getLocalContent(id) {
				for (var i = 0; i < contentList.length; i++) {
					if (contentList[i].id == id) return contentList[i];
				}
				return null;
			}
			
			function getTemplate(id) {
				for (var i = 0; i < templates.length; i++) {
					if (templates[i].id == id) return templates[i];
				}
				return null;
			}
			
			$(document).addEvent('domready', function () {
				var templateCombo = $('templates');
				templateCombo.addEvent('change', function () {
					var id = this.get('value');
					buildTemplateForm(id);
				});
				
				var contentCombo = $('contentList');
				
				new Request({
					url : 'templates.json',
					onSuccess : function (data) {
						templates = JSON.parse(data).templates;
						templateCombo.grab(new Element('option', {value:0, html : "-- Select --"}));
						for (var i = 0; i < templates.length; i++) {
							var option = new Element('option', {value : templates[i].id, html: templates[i].name});
							templateCombo.grab(option);
						}
					}
				}).send();
				
				new Request({
					url : 'content.json',
					onSuccess : function (data) {
						contentList = JSON.parse(data).contentList;
						contentList.sort(compareCourse);
						for (var i = 0; i < contentList.length; i++) {
							contentCombo.grab(createContentElement(contentList[i]));
						}
					}
				}).send();
			});
		</script>
		
		<style>
			#content {
				display: none;
			}
			
			#metaSelect {
				border-left: 1px solid rgb(150, 150, 150);
				float: right;
				margin: 25px;
				padding: 25px;
				width: 200px;
			}
		</style>
	</head>
	<body>
		<div id="metaSelect">
			<label for="templates">Template:</label>
			<br />
			<select id="templates" name="templates"></select>
			
			<br />
			
			<label>Exiting content:</label>
			<br />
			<ul id="contentList"></ul>
		</div>
		
		<form id="content" action="save.do" method="POST">
			<input type="hidden" id="contentId" name="contentId" />
			<input type="hidden" id="templateId" name="templateId" />
 			
			<label for="title">Title:</label>
			<input id="title" name="title" />
			<br />
			
			<label for="language">Language:</label>
			<input id="language" name="language" />
			<br />
			
			<div id="dynamicForm"></div>
			
			<input type="submit" value="Submit" />
		</form>
	</body>
</html>