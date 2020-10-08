package br.com.depasser.content.web.converter.impl;

import br.com.depasser.content.Content;
import br.com.depasser.content.Field;
import br.com.depasser.content.Type;
import br.com.depasser.content.field.ParentField;
import br.com.depasser.content.service.ContentService;
import br.com.depasser.content.web.converter.ConversionException;
import br.com.depasser.content.web.converter.FieldConverter;

public class ParentFieldConverter extends FieldConverter {
	
	@SuppressWarnings("unchecked")
	private static Class<? extends Field> [] classes = new Class[] {ParentField.class};
	
	private ContentService contentService;
	
	public ParentFieldConverter (ContentService contentService) {
		this.setContentService(contentService);
	}

	@Override
	public Class<? extends Field>[] getClasses() {
		return classes;
	}

	@Override
	public Field convert(Type type, String value) throws ConversionException {
		ParentField field = (ParentField) getField(type);
		
		if (value != null && !"".equals(value)) {
			String [] splitted = value.split(",");
			if (splitted.length != 2) {
				throw new ConversionException("Unexpected parent identification format: " + value);
			}
			
			int id = Integer.parseInt(splitted[0].trim());
			String language = splitted[1].trim();
			
			try {
				Content content = contentService.loadContent(id, language);
				field.setValue(content);
			} catch (Exception ex) {
				throw new ConversionException("Error while retrieving content identified by: " + value, ex);
			}
		}
		
		return field;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public ContentService getContentService() {
		return contentService;
	}

}