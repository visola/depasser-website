package br.com.depasser.content;

/**
 * <p>
 * Happens when a {@link Content#validate() content validation} finds that one
 * of the required fields is not present.
 * </p>
 *
 * @author Vinicius Isola
 */
public class MissingFieldException extends ValidationException {

	private static final long serialVersionUID = 1L;

	public MissingFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingFieldException(String message) {
		super(message);
	}

}