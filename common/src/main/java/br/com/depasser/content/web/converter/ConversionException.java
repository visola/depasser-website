package br.com.depasser.content.web.converter;

import br.com.depasser.content.Field;
import br.com.depasser.content.FieldException;

public class ConversionException extends FieldException {

	private static final long serialVersionUID = 1L;

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Field field, String message, Throwable cause) {
		super(field, message, cause);
	}

	public ConversionException(Field field, String message) {
		super(field, message);
	}

}