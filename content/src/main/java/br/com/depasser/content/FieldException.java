package br.com.depasser.content;

public class FieldException extends Exception {

	private static final long serialVersionUID = 1L;
	private final Field field;

	public FieldException(Field field, String message) {
		super(message);
		this.field = field;
	}
	
	public FieldException(Field field, String message, Throwable cause) {
		super(message, cause);
		this.field = field;
	}

	public FieldException(String message) {
		super(message);
		field = null;
	}

	public FieldException(String message, Throwable cause) {
		super(message, cause);
		field = null;
	}

	public Field getField() {
		return field;
	}
	
}