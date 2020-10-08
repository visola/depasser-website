package br.com.depasser.content;

/**
 * <p>
 * Happens when a required field is present but empty.
 * </p>
 *
 * @author Vinicius Isola
 */
public class EmptyFieldException extends ValidationException {

	private static final long serialVersionUID = -4575217719210523580L;

	public EmptyFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyFieldException(String message) {
		super(message);
	}

}