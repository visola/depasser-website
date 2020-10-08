package br.com.depasser.content.web.converter.impl;

import br.com.depasser.content.Field;
import br.com.depasser.content.Type;
import br.com.depasser.content.field.BooleanField;
import br.com.depasser.content.web.converter.ConversionException;
import br.com.depasser.content.web.converter.FieldConverter;

public class BooleanFieldConverter extends FieldConverter {

	@SuppressWarnings("unchecked")
	private static Class<? extends Field> [] classes = new Class[] {BooleanField.class};
	
	@Override
	public Class<? extends Field>[] getClasses() {
		return classes;
	}

	@Override
	public Field convert(Type type, String value) throws ConversionException {
		BooleanField field = (BooleanField) getField(type);
		if (value == null) value = "false";
		field.setValue(Boolean.parseBoolean(value));
		return field;
	}

}