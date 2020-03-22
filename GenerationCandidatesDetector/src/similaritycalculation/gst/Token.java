package similaritycalculation.gst;

import similaritycalculation.CodePiece;
import similaritycalculation.CodeUnit;

/**
 * Represents a token. A token consists of a value and information concerning
 * the position of the value in the code unit, when available.
 */
public class Token {
	/**
	 * The piece of source code corresponding to the token.
	 */
	private CodePiece codePiece;

	/** Token value. */
	private TokenValue value;

	/** String value when the token value is CUSTOM_STRING. */
	private String extraValue;

	/**
	 * Creates a new token.
	 * 
	 * @param codeUnit
	 *            Code unit the token has been extracted from.
	 * @param startPos
	 *            Start position of the token in the code unit.
	 * @param endPos
	 *            End position of the token in the code unit.
	 * @param value
	 *            Value of the token.
	 */
	public Token(CodeUnit codeUnit, int startPos, int endPos, TokenValue value) {
		this(value);
		codePiece = new CodePiece(codeUnit, startPos, endPos);
	}
	
	/**
	 * Creates a new token with a custom string value.
	 * 
	 * @param codeUnit
	 *            Code unit the token has been extracted from.
	 * @param startPos
	 *            Start position of the token in the code unit.
	 * @param endPos
	 *            End position of the token in the code unit.
	 * @param value
	 *            String value of the token.
	 */
	public Token(CodeUnit codeUnit, int startPos, int endPos, String value) {
		this(value);
		codePiece = new CodePiece(codeUnit, startPos, endPos);
	}

	/**
	 * Creates a new token.
	 * 
	 * @param value
	 *            Value of the token.
	 */
	public Token(TokenValue value) {
		this.value = value;
	}

	/**
	 * Creates a new token with a custom string value.
	 * 
	 * @param value
	 *            Value of the token.
	 */
	public Token(String value) {
		this.value = TokenValue.CUSTOM_STRING;
		this.extraValue = value;
	}

	public TokenValue getValue() {
		return value;
	}

	public void setValue(TokenValue value) {
		this.value = value;
	}

	public String getExtraValue() {
		return extraValue;
	}

	public void setExtraValue(String extraValue) {
		this.extraValue = extraValue;
	}

	public CodePiece getCodePiece() {
		return codePiece;
	}

	public void setCodePiece(CodePiece codePiece) {
		this.codePiece = codePiece;
	}
}
