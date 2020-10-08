package br.com.depasser.content;

/**
 * Validation exceptions must descend from this.
 *
 * @author Vinicius Isola
 */
public class ValidationException extends FieldException {

	private static final long serialVersionUID = 1L;

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(Field field, String message, Throwable cause) {
		super(field, message, cause);
	}

	public ValidationException(Field field, String message) {
		super(field, message);
	}

}