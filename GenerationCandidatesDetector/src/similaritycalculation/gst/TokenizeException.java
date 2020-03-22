/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.gst;

/**
 * Exceptions of this type are thrown to indicate errors during the tokenization
 * of java code.
 */
public class TokenizeException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new TokenizeException with a message.
	 * 
	 * @param message
	 *            The error message.
	 */
	public TokenizeException(final String message) {
		super(message);
	}
}
