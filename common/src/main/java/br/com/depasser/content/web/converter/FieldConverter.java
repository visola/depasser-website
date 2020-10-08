package br.com.depasser.content.web.converter;

import br.com.depasser.content.Field;
import br.com.depasser.content.Type;

public abstract class FieldConverter {
	
	public boolean canConvert(Type type) {
		String value = type.getValue();
		for (Class<? extends Field> fieldClass : getClasses()) {
			if (fieldClass.getName().equals(value)) return true;
		}
		return false;
	}
	
	protected Field getField(Type type) throws ConversionException {
		Field field = null;
		try {
			field = type.createField();
		} catch (Exception exception) {
			throw new ConversionException("Error while creating field.", exception);
		}
		
		StringBuilder classes = new StringBuilder();
		for (Class<?> fieldClass : getClasses()) {
			if (fieldClass.isInstance(field)) {
				return field;
			}
			classes.append(fieldClass.getName());
			classes.append(",");
		}
		classes.setLength( classes.length() - 1 );
		throw new ConversionException("Invalid field created: " + field.getClass() + ", expecting one of the following: " + classes.toString());
	}
	
	public abstract Class<? extends Field> [] getClasses();
	
	public abstract Field convert(Type type, String value) throws ConversionException;

}