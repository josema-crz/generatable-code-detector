package similaritycalculation.gst.asttokenizer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import similaritycalculation.gst.Token;

public class EclipseASTTokenVisitorStep10 extends ASTVisitor {
	/**
	 * Stores all {@link Block} nodes representing finally blocks. This is
	 * necessary in order to be able to create appropriate {@link TokenSymbols_Step10}
	 * when visiting block nodes (see
	 * {@link EclipseASTTokenVisitor#visit(Block)}, to be more precise in order
	 * to be able to create {@link TokenSymbols_Step10#FINALLY} and
	 * {@link TokenSymbols_Step10#FINALLY_END}. The reason for this is that in the AST
	 * there is no explicit symbol representing a finally block. Instead, a
	 * finally block is represented by a simple {@link Block} node.
	 */
	protected Set<Block> finallyBlocks;

	/**
	 * Stores all {@link Block} nodes representing method blocks. These are
	 * stored to avoid creating {@link TokenSymbols_Step10#BLOCK} and
	 * {@link TokenSymbols_Step10#BLOCK_END} when visiting blocks. Due to this, this
	 * visitor does only create a {@link TokenSymbols_Step10#METHOD_DECLARATION} symbol
	 * at the begin of the method (without subsequent {@link TokenSymbols_Step10#BLOCK}
	 * ). Accordingly, at the end of the method only
	 * {@link TokenSymbols_Step10#METHOD_DECLARATION_END} is created without preceding
	 * {@link TokenSymbols_Step10#BLOCK_END} to denote the end of the method.
	 */
	protected Set<Block> methodBlocks;

	/**
	 * Stores all {@link Statement} nodes representing the then statements of if
	 * conditions. These are stored to avoid creating {@link TokenSymbols_Step10#BLOCK}
	 * and {@link TokenSymbols_Step10#BLOCK_END} when visiting {@link IfStatement}. See
	 * documentation of {@link EclipseASTTokenVisitor#methodBlocks}.
	 */
	protected Set<Statement> thenStatementsOfIf;

	/**
	 * Stores all {@link Statement} nodes representing else statements of if
	 * conditions. This is necessary in order to be able to create appropriate
	 * {@link TokenSymbols_Step10} when visiting block nodes, to be more precise in
	 * order to be able to create {@link TokenSymbols_Step10#ELSE} and
	 * {@link TokenSymbols_Step10#ELSE_END}. The reason for this is that in the AST
	 * there is no explicit symbol representing an else statement.
	 */
	protected Set<Statement> elseStatements;

	/**
	 * Contains the number of detected type declarations in the corresponding
	 * scope. This is done in order to detect, e.g., inner class declarations.
	 * For these, the AST does not contain separate symbols. Hence, we could not
	 * differentiate between different type declarations without remembering the
	 * number of type declarations. If type declaration is detected and this
	 * counter is 0, then there has not been any type declaration before. If the
	 * counter is > 0, then this type declaration is contained within another
	 * type declaration.
	 */
	protected int typeDeclarationCounter;

	private TemplificationConfiguration templificationConfig;
	
	/**
	 * This list stores the tokens collected when visiting the AST.
	 */
	protected List<Token> tokenList;

	/**
	 * Creates an instance which initializes all fields with empty
	 * datastructures.
	 */
	public EclipseASTTokenVisitorStep10() {
		this.finallyBlocks = new HashSet<Block>();

		this.methodBlocks = new HashSet<Block>();

		this.thenStatementsOfIf = new HashSet<Statement>();
		this.elseStatements = new HashSet<Statement>();

		this.templificationConfig = TemplificationConfiguration.createDefaultConfiguration();
		
		this.tokenList = new LinkedList<Token>();
	}
	
	/**
	 * Returns the list of tokens collected by visiting the AST.
	 * 
	 * @return See description.
	 */
	public List<Token> getTokenList() {
		return this.tokenList;
	}

	/**
	 * Adds a token to {@link EclipseASTTokenVisitorDefaultStep#tokenList} with
	 * the specified value. It stores the starting and ending position on the
	 * original file, extracted from the node.
	 * 
	 * @param node
	 *            AST node for which a token is created.
	 * @param value
	 *            Value of the token.
	 */
	protected void addToken(final ASTNode node, String value) {
		this.tokenList
				.add(new Token(null, node.getStartPosition(), node.getStartPosition() + node.getLength(), value));
	}

	/**
	 * Adds a token to {@link EclipseASTTokenVisitor#tokenList}. This token
	 * refers to the position of the given node and the given symbol.
	 * 
	 * @param node
	 *            AST node for which a token is created.
	 * @param symbol
	 *            {@link TokenSymbols_Step10} representing the given node.
	 */
	public void addTokenWithSymbol(final ASTNode node, final TokenSymbols_Step10 symbol) {
		if (this.templificationConfig != null && !this.templificationConfig.shouldTokenSymbolBeFiltered(symbol)) {
			addToken(node, symbol.toString());
		}
	}

	@Override
	public boolean visit(final AnonymousClassDeclaration node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.ANONYMOUS_INNER_CLASS);
		return super.visit(node);
	}

	@Override
	public void endVisit(final AnonymousClassDeclaration node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.ANONYMOUS_INNER_CLASS_END);
		super.visit(node);
	}

	@Override
	public boolean visit(final AssertStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.ASSERT);
		return super.visit(node);
	}

	@Override
	public boolean visit(final Assignment node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.ASSIGNMENT);
		return super.visit(node);
	}

	@Override
	public boolean visit(final Block node) {
		TokenSymbols_Step10 symbol = TokenSymbols_Step10.BLOCK;
		if (this.methodBlocks.contains(node)) {
			symbol = null;
		} else if (this.finallyBlocks.contains(node)) {
			symbol = TokenSymbols_Step10.FINALLY;
		} else if (this.thenStatementsOfIf.contains(node)) {
			/* We don't want to create a block statement if the current block denotes the then part of an
			 * if-statement. The reason is that for single line statements some might put these into a
			 * block and some don't. Using these two different styles would result in a different token
			 * list which we want to avoid. */
			symbol = null;
		} else if (this.elseStatements.contains(node)) {
			symbol = TokenSymbols_Step10.ELSE;
		}

		if (symbol != null) {
			this.addTokenWithSymbol(node, symbol);
		}

		return super.visit(node);
	}

	@Override
	public void endVisit(final Block node) {
		TokenSymbols_Step10 symbol = TokenSymbols_Step10.BLOCK_END;
		if (this.methodBlocks.contains(node)) {
			symbol = null;
		} else if (this.finallyBlocks.contains(node)) {
			symbol = TokenSymbols_Step10.FINALLY_END;
		} else if (this.thenStatementsOfIf.contains(node)) {
			/* See visit(Block). */
			symbol = null;
		} else if (this.elseStatements.contains(node)) {
			symbol = TokenSymbols_Step10.ELSE_END;
		}

		if (symbol != null) {
			this.addTokenWithSymbol(node, symbol);
		}

		super.endVisit(node);
	}

	@Override
	public boolean visit(final BreakStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.BREAK);
		return super.visit(node);
	}

	@Override
	public boolean visit(final CatchClause node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.CATCH);
		return super.visit(node);
	}

	@Override
	public void endVisit(final CatchClause node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.CATCH_END);
		super.endVisit(node);
	}

	@Override
	public boolean visit(final ClassInstanceCreation node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.NEW);
		return super.visit(node);
	}

	@Override
	public boolean visit(final ConstructorInvocation node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.CONSTRUCTOR_INVOCATION_THIS);
		return super.visit(node);
	}

	@Override
	public boolean visit(final ContinueStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.CONTINUE);
		return super.visit(node);
	}

	@Override
	public boolean visit(final DoStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.DO);
		return super.visit(node);
	}

	@Override
	public void endVisit(final DoStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.DO_END);
		super.visit(node);
	}

	@Override
	public boolean visit(final EnhancedForStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.FOR);
		return super.visit(node);
	}

	@Override
	public boolean visit(final EnumDeclaration node) {
		TokenSymbols_Step10 symbol = TokenSymbols_Step10.ENUM_DECLARATION;
		if (this.typeDeclarationCounter > 0) {
			symbol = TokenSymbols_Step10.INNER_ENUM_DECLARATION;
		}

		this.addTokenWithSymbol(node, symbol);

		return super.visit(node);
	}

	@Override
	public void endVisit(final EnumDeclaration node) {
		TokenSymbols_Step10 symbol = TokenSymbols_Step10.ENUM_DECLARATION_END;
		if (this.typeDeclarationCounter > 0) {
			symbol = TokenSymbols_Step10.INNER_ENUM_DECLARATION;
		}

		this.addTokenWithSymbol(node, symbol);

		super.visit(node);
	}

	@Override
	public boolean visit(final FieldDeclaration node) {
		TokenSymbols_Step10 symbol = TokenSymbols_Step10.VARIABLE_DECLARATION;
		int modifiers = node.getModifiers();
		if (Modifier.isFinal(modifiers)) {
			symbol = TokenSymbols_Step10.CONSTANT_DECLARATION;
		}

		this.addTokenWithSymbol(node, symbol);
		return super.visit(node);
	}

	@Override
	public boolean visit(final ForStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.FOR);
		return super.visit(node);
	}

	@Override
	public void endVisit(final ForStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.FOR_END);
		super.visit(node);
	}

	@Override
	public boolean visit(final IfStatement node) {
		if (this.elseStatements.contains(node)) {
			this.addTokenWithSymbol(node, TokenSymbols_Step10.ELSE);
		}

		this.addTokenWithSymbol(node, TokenSymbols_Step10.IF);

		Statement thenStmt = node.getThenStatement();
		if (thenStmt != null) {
			this.thenStatementsOfIf.add(thenStmt);
		}

		Statement elseStmt = node.getElseStatement();
		if (elseStmt != null) {
			this.elseStatements.add(elseStmt);
		}

		return super.visit(node);
	}

	@Override
	public void endVisit(final IfStatement node) {
		if (this.elseStatements.contains(node)) {
			this.addTokenWithSymbol(node, TokenSymbols_Step10.ELSE_END);
		}

		this.addTokenWithSymbol(node, TokenSymbols_Step10.IF_END);
		super.endVisit(node);
	}

	@Override
	public boolean visit(final ImportDeclaration node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.IMPORT_DECLARATION);
		return super.visit(node);
	}

	@Override
	public boolean visit(final Initializer node) {
		int modifiers = node.getModifiers();
		if (Modifier.isStatic(modifiers)) {
			this.addTokenWithSymbol(node, TokenSymbols_Step10.STATIC_INITIALIZATION);
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(final MethodDeclaration node) {
		TokenSymbols_Step10 symbol = TokenSymbols_Step10.METHOD_DECLARATION;
		if (node.isConstructor()) {
			symbol = TokenSymbols_Step10.CONSTRUCTOR_DECLARATION;
		} else if (node.getBody() == null) {
			symbol = TokenSymbols_Step10.ABSTRACT_METHOD_DECLARATION;
		}

		if (node.getBody() != null) {
			this.methodBlocks.add(node.getBody());
		}

		this.addTokenWithSymbol(node, symbol);
		return super.visit(node);
	}

	@Override
	public void endVisit(final MethodDeclaration node) {
		TokenSymbols_Step10 symbol = TokenSymbols_Step10.METHOD_DECLARATION_END;
		if (node.isConstructor()) {
			symbol = TokenSymbols_Step10.CONSTRUCTOR_DECLARATION_END;
		} else if (node.getBody() == null) {
			symbol = null;
		}

		if (symbol != null) {
			this.addTokenWithSymbol(node, symbol);
		}

		super.endVisit(node);
	}

	@Override
	public boolean visit(final MethodInvocation node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.METHOD_INVOCATION);
		return super.visit(node);
	}

	@Override
	public boolean visit(final PackageDeclaration node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.PACKAGE_DECLARATION);
		return super.visit(node);
	}

	@Override
	public boolean visit(final ReturnStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.RETURN);
		return super.visit(node);
	}

	@Override
	public boolean visit(final SuperConstructorInvocation node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.CONSTRUCTOR_INVOCATION_SUPER);
		return super.visit(node);
	}

	@Override
	public boolean visit(final SuperMethodInvocation node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.METHOD_INVOCATION);
		return super.visit(node);
	}

	@Override
	public boolean visit(final SwitchCase node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.CASE);
		return super.visit(node);
	}

	@Override
	public boolean visit(final SwitchStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.SWITCH);
		return super.visit(node);
	}

	@Override
	public void endVisit(final SwitchStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.SWITCH_END);
	}

	@Override
	public boolean visit(final SynchronizedStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.SYNCHRONIZED);
		return super.visit(node);
	}

	@Override
	public void endVisit(final SynchronizedStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.SYNCHRONIZED_END);
		super.endVisit(node);
	}

	@Override
	public boolean visit(final ThrowStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.THROW);
		return super.visit(node);
	}

	@Override
	public boolean visit(final TryStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.TRY);
		if (node.getFinally() != null) {
			this.finallyBlocks.add(node.getFinally());
		}

		return super.visit(node);
	}

	@Override
	public void endVisit(final TryStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.TRY_END);
		super.visit(node);
	}

	@Override
	public boolean visit(final TypeDeclaration node) {
		this.typeDeclarationCounter++;

		TokenSymbols_Step10 symbol = TokenSymbols_Step10.CLASS_DECLARATION;
		if (node.isInterface()) {
			if (this.typeDeclarationCounter > 1) {
				symbol = TokenSymbols_Step10.INNER_INTERFACE_DECLARATION;
			} else {
				symbol = TokenSymbols_Step10.INTERFACE_DECLARATION;
			}
		} else if (this.typeDeclarationCounter > 1) {
			symbol = TokenSymbols_Step10.INNER_CLASS_DECLARATION;
		}

		this.addTokenWithSymbol(node, symbol);
		return super.visit(node);
	}

	@Override
	public void endVisit(final TypeDeclaration node) {
		this.typeDeclarationCounter--;

		TokenSymbols_Step10 symbol = TokenSymbols_Step10.CLASS_DECLARATION_END;
		if (node.isInterface()) {
			if (this.typeDeclarationCounter > 0) {
				symbol = TokenSymbols_Step10.INNER_INTERFACE_DECLARATION_END;
			} else {
				symbol = TokenSymbols_Step10.INTERFACE_DECLARATION_END;
			}

		} else if (this.typeDeclarationCounter > 0) {
			symbol = TokenSymbols_Step10.INNER_CLASS_DECLARATION_END;
		}

		this.addTokenWithSymbol(node, symbol);
		super.endVisit(node);
	}

	@Override
	public boolean visit(final VariableDeclarationStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.VARIABLE_DECLARATION);
		return super.visit(node);
	}

	@Override
	public boolean visit(final WhileStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.WHILE);
		return super.visit(node);
	}

	@Override
	public void endVisit(final WhileStatement node) {
		this.addTokenWithSymbol(node, TokenSymbols_Step10.WHILE_END);
		super.visit(node);
	}
}
