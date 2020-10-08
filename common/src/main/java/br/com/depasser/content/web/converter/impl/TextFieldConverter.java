package br.com.depasser.content.web.converter.impl;

import br.com.depasser.content.Field;
import br.com.depasser.content.Type;
import br.com.depasser.content.field.LongTextField;
import br.com.depasser.content.field.TextField;
import br.com.depasser.content.web.converter.ConversionException;
import br.com.depasser.content.web.converter.FieldConverter;

public class TextFieldConverter extends FieldConverter {

	@SuppressWarnings("unchecked")
	private static Class<? extends Field> [] classes = new Class[] {TextField.class, LongTextField.class};
	
	@Override
	public Field convert(Type type, String value) throws ConversionException {
		Field field = getField(type);
		if (field instanceof TextField) {
			( (TextField) field ).setValue(value);
		} else {
			( (LongTextField) field ).setValue(value);
		}
		return field;
	}

	@Override
	public Class<? extends Field>[] getClasses() {
		return classes;
	}

}