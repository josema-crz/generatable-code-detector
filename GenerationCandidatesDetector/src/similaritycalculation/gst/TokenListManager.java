/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.gst;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for created token lists.
 * 
 * TODO It is currently not being used.
 */
public class TokenListManager {

	private Map<File, List<Token>> fileTokenListMapping;

	private static final TokenListManager INSTANCE = new TokenListManager();

	private TokenListManager() {
		this.fileTokenListMapping = new HashMap<File, List<Token>>();
	}

	public static TokenListManager getInstance() {
		return INSTANCE;
	}

	public List<Token> getTokenListForFile(File f) {
		/*
		 * List<Token> storedTL = this.fileTokenListMapping.get(f); if (storedTL
		 * != null) { return storedTL; }
		 * 
		 * ITokenizer tokenizer = new EclipseASTTokenizer(); try { List<Token>
		 * newTokenList = tokenizer.tokenize(f);
		 * this.fileTokenListMapping.put(f, newTokenList); return newTokenList;
		 * } catch (TokenizeException te) {
		 * 
		 * }
		 */
		return null;
	}
}
