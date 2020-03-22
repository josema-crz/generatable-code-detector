/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.gst.asttokenizer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import similaritycalculation.CodeUnit;
import similaritycalculation.gst.Token;
import similaritycalculation.gst.TokenValue;

/**
 * Visitor representing the functionality of the basic, initial pre-processing
 * step. All the original code is kept. This will be the basis on which the rest
 * of steps will be built.
 */
public class EclipseASTTokenVisitorDefaultStep extends ASTVisitor {
	/**
	 * Model class related to the code unit. It can be null, but some
	 * pre-processing actions may not be effective depending on the step.
	 */
	protected IType modelClass;

	/**
	 * This list stores the tokens collected when visiting the AST.
	 */
	protected List<Token> tokenList;

	/**
	 * The code unit whose content is being visited.
	 */
	protected CodeUnit codeUnit;

	/**
	 * Creates a visitor for the default, initial step.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @param modelClass
	 *            Model class related to the code unit. It can be null, but some
	 *            pre-processing actions may not be effective depending on the
	 *            step.
	 */
	public EclipseASTTokenVisitorDefaultStep(CodeUnit codeUnit, IType modelClass) {
		this.codeUnit = codeUnit;
		this.tokenList = new LinkedList<Token>();
		this.modelClass = modelClass;
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
	 * the specified token value. It stores the starting and ending position on
	 * the original file, extracted from the node.
	 * 
	 * @param node
	 *            AST node for which a token is created.
	 * @param value
	 *            Value of the token.
	 */
	protected void addToken(final ASTNode node, TokenValue value) {
		this.tokenList
				.add(new Token(codeUnit, node.getStartPosition(), node.getStartPosition() + node.getLength(), value));
	}

	/**
	 * Adds a token to {@link EclipseASTTokenVisitorDefaultStep#tokenList} with
	 * the specified token value. It doesn't store the positions on the original
	 * file.
	 * 
	 * @param node
	 *            AST node for which a token is created.
	 * @param value
	 *            Value of the token.
	 */
	protected void addToken(TokenValue value) {
		this.tokenList.add(new Token(value));
	}

	/**
	 * Adds a token to {@link EclipseASTTokenVisitorDefaultStep#tokenList} with
	 * the specified custom string value. It stores the starting and ending
	 * position on the original file, extracted from the node.
	 * 
	 * @param node
	 *            AST node for which a token is created.
	 * @param value
	 *            Value of the token.
	 */
	protected void addToken(final ASTNode node, String value) {
		this.tokenList
				.add(new Token(codeUnit, node.getStartPosition(), node.getStartPosition() + node.getLength(), value));
	}

	/**
	 * Adds a token to {@link EclipseASTTokenVisitorDefaultStep#tokenList} with
	 * the specified custom string value. It doesn't store the positions on the
	 * original file.
	 * 
	 * @param node
	 *            AST node for which a token is created.
	 * @param value
	 *            Value of the token.
	 */
	protected void addToken(String value) {
		this.tokenList.add(new Token(value));
	}

	/**
	 * There is two addToken methods. When adding a token that comes from a
	 * specific node, we can pass this node and its position in the original
	 * file will be saved with the token. When adding a structural element (e.g.
	 * keywords, parenthesis, brackets) we don't know exactly the position of
	 * the corresponding element in the original file, so we will create a token
	 * without this information.
	 */

	/**
	 * Visits a list of nodes, adding a token separator between them.
	 */
	protected void visitList(List nodes, TokenValue separator) {
		if (!nodes.isEmpty()) {
			// Visit the first one
			((ASTNode) nodes.get(0)).accept(this);
			// Visit the rest, adding the separator before each node
			for (int i = 1; i < nodes.size(); i++) {
				addToken(separator);
				((ASTNode) nodes.get(i)).accept(this);
			}
		}
	}

	/**
	 * Visits a list of nodes, adding a string separator between them.
	 */
	protected void visitList(List nodes, String separator) {
		if (!nodes.isEmpty()) {
			// Visit the first one
			((ASTNode) nodes.get(0)).accept(this);
			// Visit the rest, adding the separator before each node
			for (int i = 1; i < nodes.size(); i++) {
				addToken(separator);
				((ASTNode) nodes.get(i)).accept(this);
			}
		}
	}

	/**
	 * Visits a list of nodes.
	 */
	protected void visitList(List nodes) {
		for (Object node : nodes) {
			((ASTNode) node).accept(this);
		}
	}

	/**
	 * AnnotationTypeDeclaration: [ Javadoc ] { ExtendedModifier } @ interface
	 * Identifier { { AnnotationTypeBodyDeclaration | ; } }
	 */
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		addToken(TokenValue.AT);
		addToken(TokenValue.INTERFACE);
		node.getName().accept(this);
		addToken(TokenValue.OPENING_BRACKET);
		if (node.bodyDeclarations().isEmpty()) {
			addToken(TokenValue.SEMICOLON);
		} else {
			visitList(node.bodyDeclarations());
		}
		addToken(TokenValue.CLOSING_BRACKET);

		return false;
	}

	/**
	 * AnnotationTypeMemberDeclaration: [ Javadoc ] { ExtendedModifier } Type
	 * Identifier ( ) [ default Expression ] ;
	 */
	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		node.getType().accept(this);
		node.getName().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		if (node.getDefault() != null) {
			addToken(TokenValue.DEFAULT);
			node.getDefault().accept(this);
		}
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * AnonymousClassDeclaration: { ClassBodyDeclaration }
	 */
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		addToken(TokenValue.OPENING_BRACKET);
		visitList(node.bodyDeclarations());
		addToken(TokenValue.CLOSING_BRACKET);

		return false;
	}

	/**
	 * ArrayAccess: Expression [ Expression ]
	 */
	@Override
	public boolean visit(ArrayAccess node) {
		node.getArray().accept(this);
		addToken(TokenValue.OPENING_SQUARE_BRACKET);
		node.getIndex().accept(this);
		addToken(TokenValue.CLOSING_SQUARE_BRACKET);

		return false;
	}

	/**
	 * ArrayCreation: new PrimitiveType [ Expression ] { [ Expression ] } { [ ]
	 * } new TypeName [ < Type { , Type } > ] [ Expression ] { [ Expression ] }
	 * { [ ] } new PrimitiveType [ ] { [ ] } ArrayInitializer new TypeName [ <
	 * Type { , Type } > ] [ ] { [ ] } ArrayInitializer
	 */
	@Override
	public boolean visit(ArrayCreation node) {
		addToken(TokenValue.NEW);
		node.getType().accept(this);
		// Dimensions omitted.
		if (node.getInitializer() != null) {
			node.getInitializer().accept(this);
		}

		return false;
	}

	/**
	 * ArrayInitializer: { [ Expression { , Expression} [ , ]] }
	 */
	@Override
	public boolean visit(ArrayInitializer node) {
		addToken(TokenValue.OPENING_BRACKET);
		// We don't adjust the number of expressions in relation to the number
		// of dimensions, just process the ones defined.
		visitList(node.expressions(), TokenValue.COMMA);
		addToken(TokenValue.CLOSING_BRACKET);

		return false;
	}

	/**
	 * ArrayType: Type Dimension { Dimension }
	 */
	@Override
	public boolean visit(ArrayType node) {
		node.getElementType().accept(this);
		visitList(node.dimensions());

		return false;
	}

	/**
	 * AssertStatement: assert Expression [ : Expression ] ;
	 */
	@Override
	public boolean visit(AssertStatement node) {
		addToken(TokenValue.ASSERT);
		node.getExpression().accept(this);
		if (node.getMessage() != null) {
			addToken(TokenValue.COLON);
			node.getMessage().accept(this);
		}
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * Assignment: Expression AssignmentOperator Expression
	 */
	@Override
	public boolean visit(Assignment node) {
		node.getLeftHandSide().accept(this);
		addToken(node.getOperator().toString());
		node.getRightHandSide().accept(this);

		return false;
	}

	/**
	 * Block: { { Statement } }
	 */
	@Override
	public boolean visit(Block node) {
		addToken(TokenValue.OPENING_BRACKET);
		visitList(node.statements());
		addToken(TokenValue.CLOSING_BRACKET);

		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(BlockComment node) {
		addToken(node, node.toString());
		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(BooleanLiteral node) {
		addToken(node, node.toString());
		return false;
	}

	/**
	 * BreakStatement: break [ Identifier ] ;
	 */
	@Override
	public boolean visit(BreakStatement node) {
		addToken(TokenValue.BREAK);
		if (node.getLabel() != null) {
			node.getLabel().accept(this);
		}
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * CastExpression: ( Type ) Expression
	 */
	@Override
	public boolean visit(CastExpression node) {
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getType().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		node.getExpression().accept(this);

		return false;
	}

	/**
	 * CatchClause: catch ( FormalParameter ) Block
	 */
	@Override
	public boolean visit(CatchClause node) {
		addToken(TokenValue.CATCH);
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getException().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		node.getBody().accept(this);

		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(CharacterLiteral node) {
		addToken(node, node.toString());
		return false;
	}

	/**
	 * ClassInstanceCreation: [ Expression . ] new [ < Type { , Type } > ] Type
	 * ( [ Expression { , Expression } ] ) [ AnonymousClassDeclaration ]
	 */
	@Override
	public boolean visit(ClassInstanceCreation node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			addToken(TokenValue.DOT);
		}
		addToken(TokenValue.NEW);
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getType().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		visitList(node.arguments(), TokenValue.COMMA);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		if (node.getAnonymousClassDeclaration() != null) {
			node.getAnonymousClassDeclaration().accept(this);
		}

		return false;
	}

	/**
	 * CompilationUnit: [ PackageDeclaration ] { ImportDeclaration } {
	 * TypeDeclaration | EnumDeclaration | AnnotationTypeDeclaration | ; }
	 */
	@Override
	public boolean visit(CompilationUnit node) {
		if (node.getPackage() != null) {
			node.getPackage().accept(this);
		}
		visitList(node.imports());
		if (node.types().isEmpty()) {
			addToken(TokenValue.SEMICOLON);
		} else {
			visitList(node.types());
		}

		return false;
	}

	/**
	 * ConditionalExpression: Expression ? Expression : Expression
	 */
	@Override
	public boolean visit(ConditionalExpression node) {
		node.getExpression().accept(this);
		addToken(TokenValue.INTERROGATION_MARK);
		node.getThenExpression().accept(this);
		addToken(TokenValue.COLON);
		node.getElseExpression().accept(this);

		return false;
	}

	/**
	 * ConstructorInvocation: [ < Type { , Type } > ] this ( [ Expression { ,
	 * Expression } ] ) ;
	 */
	@Override
	public boolean visit(ConstructorInvocation node) {
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		addToken(TokenValue.THIS);
		addToken(TokenValue.OPENING_PARENTHESIS);
		visitList(node.arguments(), TokenValue.COMMA);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * ContinueStatement: continue [ Identifier ] ;
	 */
	@Override
	public boolean visit(ContinueStatement node) {
		addToken(TokenValue.CONTINUE);
		if (node.getLabel() != null) {
			node.getLabel().accept(this);
		}
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * CreationReference: Type :: [ < Type { , Type } > ] new
	 */
	@Override
	public boolean visit(CreationReference node) {
		node.getType().accept(this);
		addToken(TokenValue.DOUBLE_COLON);
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		addToken(TokenValue.NEW);

		return false;
	}

	/**
	 * Dimension: { Annotation } []
	 */
	@Override
	public boolean visit(Dimension node) {
		for (Object annotation : node.annotations()) {
			((ASTNode) annotation).accept(this);
		}
		addToken(TokenValue.EMPTY_SQUARE_BRACKETS);

		return false;
	}

	/**
	 * DoStatement: do Statement while ( Expression ) ;
	 */
	@Override
	public boolean visit(DoStatement node) {
		addToken(TokenValue.DO);
		node.getBody().accept(this);
		addToken(TokenValue.WHILE);
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getExpression().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * EmptyStatement: ;
	 */
	@Override
	public boolean visit(EmptyStatement node) {
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * EnhancedForStatement: for ( FormalParameter : Expression ) Statement
	 */
	@Override
	public boolean visit(EnhancedForStatement node) {
		addToken(TokenValue.FOR);
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getParameter().accept(this);
		addToken(TokenValue.COLON);
		node.getExpression().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		node.getBody().accept(this);

		return false;
	}

	/**
	 * EnumConstantDeclaration: [ Javadoc ] { ExtendedModifier } Identifier [ (
	 * [ Expression { , Expression } ] ) ] [ AnonymousClassDeclaration ]
	 */
	@Override
	public boolean visit(EnumConstantDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		node.getName().accept(this);
		if (!node.arguments().isEmpty()) {
			addToken(TokenValue.OPENING_PARENTHESIS);
			visitList(node.arguments(), TokenValue.COMMA);
			addToken(TokenValue.CLOSING_PARENTHESIS);
		}
		if (node.getAnonymousClassDeclaration() != null) {
			node.getAnonymousClassDeclaration().accept(this);
		}

		return false;
	}

	/**
	 * EnumDeclaration: [ Javadoc ] { ExtendedModifier } enum Identifier [
	 * implements Type { , Type } ] { [ EnumConstantDeclaration { ,
	 * EnumConstantDeclaration } ] [ , ] [ ; { ClassBodyDeclaration | ; } ] }
	 */
	@Override
	public boolean visit(EnumDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		addToken(TokenValue.ENUM);
		node.getName().accept(this);
		if (!node.superInterfaceTypes().isEmpty()) {
			addToken(TokenValue.IMPLEMENTS);
			visitList(node.superInterfaceTypes(), TokenValue.COMMA);
		}
		addToken(TokenValue.OPENING_BRACKET);
		visitList(node.enumConstants(), TokenValue.COMMA);
		addToken(TokenValue.SEMICOLON);
		visitList(node.bodyDeclarations());
		addToken(TokenValue.CLOSING_BRACKET);

		return false;
	}

	/**
	 * ExpressionMethodReference: Expression :: [ < Type { , Type } > ]
	 * Identifier
	 */
	@Override
	public boolean visit(ExpressionMethodReference node) {
		node.getExpression().accept(this);
		addToken(TokenValue.DOUBLE_COLON);
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getName().accept(this);

		return false;
	}

	/**
	 * ExpressionStatement: StatementExpression ;
	 */
	@Override
	public boolean visit(ExpressionStatement node) {
		node.getExpression().accept(this);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * FieldAccess: Expression . Identifier
	 */
	@Override
	public boolean visit(FieldAccess node) {
		node.getExpression().accept(this);
		addToken(TokenValue.DOT);
		node.getName().accept(this);

		return false;
	}

	/**
	 * FieldDeclaration: [Javadoc] { ExtendedModifier } Type
	 * VariableDeclarationFragment { , VariableDeclarationFragment } ;
	 */
	@Override
	public boolean visit(FieldDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		node.getType().accept(this);
		visitList(node.fragments(), TokenValue.COMMA);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * ForStatement: for ( [ ForInit ]; [ Expression ] ; [ ForUpdate ] )
	 * Statement
	 */
	@Override
	public boolean visit(ForStatement node) {
		addToken(TokenValue.FOR);
		addToken(TokenValue.OPENING_PARENTHESIS);
		List initializers = (List) node.getStructuralProperty(ForStatement.INITIALIZERS_PROPERTY);
		List updaters = (List) node.getStructuralProperty(ForStatement.UPDATERS_PROPERTY);
		visitList(initializers, TokenValue.COMMA);
		addToken(TokenValue.SEMICOLON);
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		addToken(TokenValue.SEMICOLON);
		visitList(updaters, TokenValue.COMMA);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		node.getBody().accept(this);

		return false;
	}

	/**
	 * IfStatement: if ( Expression ) Statement [ else Statement]
	 */
	@Override
	public boolean visit(IfStatement node) {
		addToken(TokenValue.IF);
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getExpression().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		node.getThenStatement().accept(this);
		if (node.getElseStatement() != null) {
			addToken(TokenValue.ELSE);
			node.getElseStatement().accept(this);
		}

		return false;
	}

	/**
	 * ImportDeclaration: import [ static ] Name [ . * ] ;
	 */
	@Override
	public boolean visit(ImportDeclaration node) {
		addToken(TokenValue.IMPORT);
		if (node.isStatic()) {
			addToken(TokenValue.STATIC);
		}
		node.getName().accept(this);
		if (node.isOnDemand()) {
			addToken(TokenValue.DOT_ASTERISK);
		}
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * InfixExpression: Expression InfixOperator Expression { InfixOperator
	 * Expression }
	 */
	@Override
	public boolean visit(InfixExpression node) {
		node.getLeftOperand().accept(this);
		addToken(node.getOperator().toString());
		node.getRightOperand().accept(this);
		if (node.hasExtendedOperands()) {
			addToken(node.getOperator().toString());
			visitList(node.extendedOperands(), node.getOperator().toString());
		}

		return false;
	}

	/**
	 * Initializer: [ static ] Block
	 */
	@Override
	public boolean visit(Initializer node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		node.getBody().accept(this);

		return false;
	}

	/**
	 * InstanceofExpression: Expression instanceof Type
	 */
	@Override
	public boolean visit(InstanceofExpression node) {
		node.getLeftOperand().accept(this);
		addToken(TokenValue.INSTANCEOF);
		node.getRightOperand().accept(this);

		return false;
	}

	/**
	 * Type & Type { & Type }
	 */
	@Override
	public boolean visit(IntersectionType node) {
		visitList(node.types(), TokenValue.AMPERSAND);
		return false;
	}

	/**
	 * Javadoc: \/** { TagElement } *\/
	 */
	@Override
	public boolean visit(Javadoc node) {
		visitList(node.tags());
		return false;
	}

	/**
	 * LabeledStatement: Identifier : Statement
	 */
	@Override
	public boolean visit(LabeledStatement node) {
		node.getLabel().accept(this);
		addToken(TokenValue.COLON);
		node.getBody().accept(this);

		return false;
	}

	/**
	 * LambdaExpression: Identifier -> Body ( [ Identifier { , Identifier } ] )
	 * -> Body ( [ FormalParameter { , FormalParameter } ] ) -> Body
	 */
	@Override
	public boolean visit(LambdaExpression node) {
		if (node.hasParentheses()) {
			addToken(TokenValue.OPENING_PARENTHESIS);
		}
		visitList(node.parameters(), TokenValue.COMMA);
		if (node.hasParentheses()) {
			addToken(TokenValue.CLOSING_PARENTHESIS);
		}
		addToken(TokenValue.ARROW);
		node.getBody().accept(this);

		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(LineComment node) {
		addToken(node, node.toString());
		return false;
	}

	/**
	 * MarkerAnnotation: @ TypeName
	 */
	@Override
	public boolean visit(MarkerAnnotation node) {
		addToken(TokenValue.AT);
		node.getTypeName().accept(this);

		return false;
	}

	/**
	 * MemberRef: [ Name ] # Identifier
	 */
	@Override
	public boolean visit(MemberRef node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
		}
		addToken(TokenValue.HASH);
		node.getName().accept(this);

		return false;
	}

	/**
	 * MemberValuePair: SimpleName = Expression
	 */
	@Override
	public boolean visit(MemberValuePair node) {
		node.getName().accept(this);
		addToken(TokenValue.EQUAL);
		node.getValue().accept(this);

		return false;
	}

	/**
	 * MethodRef: [ Name ] # Identifier ( [ MethodRefParameter | { ,
	 * MethodRefParameter } ] )
	 */
	@Override
	public boolean visit(MethodRef node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
		}
		addToken(TokenValue.HASH);
		node.getName().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		visitList(node.parameters(), TokenValue.COMMA);
		addToken(TokenValue.CLOSING_PARENTHESIS);

		return false;
	}

	/**
	 * MethodRefParameter: Type [ ... ] [ Identifier ]
	 */
	@Override
	public boolean visit(MethodRefParameter node) {
		node.getType().accept(this);
		if (node.isVarargs()) {
			addToken(TokenValue.THREE_DOTS);
		}
		if (node.getName() != null) {
			node.getName().accept(this);
		}

		return false;
	}

	/**
	 * MethodDeclaration: [ Javadoc ] { ExtendedModifier } [ < TypeParameter { ,
	 * TypeParameter } > ] ( Type | void ) Identifier ( [ ReceiverParameter , ]
	 * [ FormalParameter { , FormalParameter } ] ) { Dimension } [ throws Type {
	 * , Type } ] ( Block | ; )
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		if (!node.typeParameters().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeParameters(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		if (node.getReturnType2() != null) {
			node.getReturnType2().accept(this);
		} else if (!node.isConstructor()) {
			addToken(TokenValue.VOID);
		}
		node.getName().accept(this);
		addToken(node, TokenValue.OPENING_PARENTHESIS);
		if (node.getReceiverType() != null) {
			node.getReceiverType().accept(this);
		}
		if (node.getReceiverQualifier() != null) {
			node.getReceiverQualifier().accept(this);
		}
		visitList(node.parameters(), TokenValue.COMMA);
		addToken(node, TokenValue.CLOSING_PARENTHESIS);
		visitList(node.extraDimensions());
		if (!node.thrownExceptionTypes().isEmpty()) {
			addToken(TokenValue.THROWS);
			visitList(node.thrownExceptionTypes(), TokenValue.COMMA);
		}
		if (node.getBody() != null) {
			node.getBody().accept(this);
		} else {
			addToken(TokenValue.SEMICOLON);
		}

		return false;
	}

	/**
	 * MethodInvocation: [ Expression . ] [ < Type { , Type } > ] Identifier ( [
	 * Expression { , Expression } ] )
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			addToken(TokenValue.DOT);
		}
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getName().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		visitList(node.arguments(), TokenValue.COMMA);
		addToken(TokenValue.CLOSING_PARENTHESIS);

		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(Modifier node) {
		addToken(node, node.toString());
		return false;
	}

	/**
	 * NameQualifiedType: Name . { Annotation } SimpleName
	 */
	@Override
	public boolean visit(NameQualifiedType node) {
		node.getQualifier().accept(this);
		addToken(TokenValue.DOT);
		visitList(node.annotations());
		node.getName().accept(this);

		return false;
	}

	/**
	 * NormalAnnotation: @ TypeName ( [ MemberValuePair { , MemberValuePair } ]
	 * )
	 */
	@Override
	public boolean visit(NormalAnnotation node) {
		addToken(TokenValue.AT);
		node.getTypeName().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		visitList(node.values(), TokenValue.COMMA);
		addToken(TokenValue.CLOSING_PARENTHESIS);

		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(NullLiteral node) {
		addToken(node, node.toString());
		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(NumberLiteral node) {
		addToken(node, node.toString());
		return false;
	}

	/**
	 * PackageDeclaration: [ Javadoc ] { Annotation } package Name ;
	 */
	@Override
	public boolean visit(PackageDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.annotations());
		addToken(TokenValue.PACKAGE);
		node.getName().accept(this);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * ParameterizedType: Type < Type { , Type } >
	 */
	@Override
	public boolean visit(ParameterizedType node) {
		node.getType().accept(this);
		addToken(TokenValue.LESS_THAN);
		visitList(node.typeArguments(), TokenValue.COMMA);
		addToken(TokenValue.GREATER_THAN);

		return false;
	}

	/**
	 * ParenthesizedExpression: ( Expression )
	 */
	@Override
	public boolean visit(ParenthesizedExpression node) {
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getExpression().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);

		return false;
	}

	/**
	 * PostfixExpression: Expression PostfixOperator
	 */
	@Override
	public boolean visit(PostfixExpression node) {
		node.getOperand().accept(this);
		addToken(node.getOperator().toString());

		return false;
	}

	/**
	 * PrefixExpression: PrefixOperator Expression
	 */
	@Override
	public boolean visit(PrefixExpression node) {
		addToken(node.getOperator().toString());
		node.getOperand().accept(this);

		return false;
	}

	/**
	 * PrimitiveType: { Annotation } byte|short|...
	 */
	@Override
	public boolean visit(PrimitiveType node) {
		visitList(node.annotations());
		addToken(node, node.toString());

		return false;
	}

	/**
	 * QualifiedName: Name . SimpleName
	 */
	@Override
	public boolean visit(QualifiedName node) {
		node.getQualifier().accept(this);
		addToken(TokenValue.DOT);
		node.getName().accept(this);

		return false;
	}

	/**
	 * QualifiedType: Type . { Annotation } SimpleName
	 */
	@Override
	public boolean visit(QualifiedType node) {
		node.getQualifier().accept(this);
		addToken(TokenValue.DOT);
		visitList(node.annotations());
		node.getName().accept(this);

		return false;
	}

	/**
	 * ReturnStatement: return [ Expression ] ;
	 */
	@Override
	public boolean visit(ReturnStatement node) {
		addToken(TokenValue.RETURN);
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * SimpleName: Identifier
	 */
	@Override
	public boolean visit(SimpleName node) {
		addToken(node, node.getIdentifier());
		return false;
	}

	/**
	 * SimpleType: { Annotation } TypeName
	 */
	@Override
	public boolean visit(SimpleType node) {
		visitList(node.annotations());
		node.getName().accept(this);

		return false;
	}

	/**
	 * SingleMemberAnnotation: @ TypeName ( Expression )
	 */
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		addToken(TokenValue.AT);
		node.getTypeName().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getValue().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);

		return false;
	}

	/**
	 * SingleVariableDeclaration: { ExtendedModifier } Type {Annotation} [ ... ]
	 * Identifier { Dimension } [ = Expression ]
	 */
	@Override
	public boolean visit(SingleVariableDeclaration node) {
		visitList(node.modifiers());
		node.getType().accept(this);
		visitList(node.varargsAnnotations());
		if (node.isVarargs()) {
			addToken(TokenValue.THREE_DOTS);
		}
		node.getName().accept(this);
		visitList(node.extraDimensions());
		if (node.getInitializer() != null) {
			addToken(TokenValue.EQUAL);
			node.getInitializer().accept(this);
		}

		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(StringLiteral node) {
		addToken(node, node.getLiteralValue());
		return false;
	}

	/**
	 * SuperConstructorInvocation: [ Expression . ] [ < Type { , Type } > ]
	 * super ( [ Expression { , Expression } ] ) ;
	 */
	@Override
	public boolean visit(SuperConstructorInvocation node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			addToken(TokenValue.DOT);
		}
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		addToken(TokenValue.SUPER);
		addToken(TokenValue.OPENING_PARENTHESIS);
		visitList(node.arguments(), TokenValue.COMMA);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * SuperFieldAccess: [ ClassName . ] super . Identifier
	 */
	@Override
	public boolean visit(SuperFieldAccess node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			addToken(TokenValue.DOT);
		}
		addToken(TokenValue.SUPER);
		addToken(TokenValue.DOT);
		node.getName().accept(this);

		return false;
	}

	/**
	 * SuperMethodInvocation: [ ClassName . ] super . [ < Type { , Type } > ]
	 * Identifier ( [ Expression { , Expression } ] )
	 */
	@Override
	public boolean visit(SuperMethodInvocation node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			addToken(TokenValue.DOT);
		}
		addToken(TokenValue.SUPER);
		addToken(TokenValue.DOT);
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getName().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		visitList(node.arguments(), TokenValue.COMMA);
		addToken(TokenValue.CLOSING_PARENTHESIS);

		return false;
	}

	/**
	 * SuperMethodReference: [ ClassName . ] super :: [ < Type { , Type } > ]
	 * Identifier
	 */
	@Override
	public boolean visit(SuperMethodReference node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			addToken(TokenValue.DOT);
		}
		addToken(TokenValue.SUPER);
		addToken(TokenValue.DOUBLE_COLON);
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getName().accept(this);

		return false;
	}

	/**
	 * SwitchCase: case Expression : default :
	 */
	@Override
	public boolean visit(SwitchCase node) {
		if (node.isDefault()) {
			addToken(TokenValue.DEFAULT);
			addToken(TokenValue.COLON);
		} else {
			addToken(TokenValue.CASE);
			node.getExpression().accept(this);
			addToken(TokenValue.COLON);
		}

		return false;
	}

	/**
	 * SwitchStatement: switch ( Expression ) { { SwitchCase | Statement } }
	 */
	@Override
	public boolean visit(SwitchStatement node) {
		addToken(TokenValue.SWITCH);
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getExpression().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		addToken(TokenValue.OPENING_BRACKET);
		visitList(node.statements());
		addToken(TokenValue.CLOSING_BRACKET);

		return false;
	}

	/**
	 * SynchronizedStatement: synchronized ( Expression ) Block
	 */
	@Override
	public boolean visit(SynchronizedStatement node) {
		addToken(TokenValue.SYNCHRONIZED);
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getExpression().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		node.getBody().accept(this);

		return false;
	}

	/**
	 * TagElement: [ @ Identifier ] { DocElement }
	 */
	@Override
	public boolean visit(TagElement node) {
		if (node.getTagName() != null) {
			addToken(node.getTagName());
		}
		visitList(node.fragments());

		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean visit(TextElement node) {
		addToken(node, node.toString());
		return false;
	}

	/**
	 * ThisExpression: [ ClassName . ] this
	 */
	@Override
	public boolean visit(ThisExpression node) {
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			addToken(TokenValue.DOT);
		}
		addToken(TokenValue.THIS);

		return false;
	}

	/**
	 * ThrowStatement: throw Expression ;
	 */
	@Override
	public boolean visit(ThrowStatement node) {
		addToken(TokenValue.THROW);
		node.getExpression().accept(this);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * TryStatement: try [ ( Resources ) ] Block [ { CatchClause } ] [ finally
	 * Block ]
	 */
	@Override
	public boolean visit(TryStatement node) {
		addToken(TokenValue.TRY);
		if (!node.resources().isEmpty()) {
			addToken(TokenValue.OPENING_PARENTHESIS);
			visitList(node.resources());
			addToken(TokenValue.CLOSING_PARENTHESIS);
		}
		node.getBody().accept(this);
		visitList(node.catchClauses());
		if (node.getFinally() != null) {
			addToken(TokenValue.FINALLY);
			node.getFinally().accept(this);
		}

		return false;
	}

	/**
	 * TypeDeclaration: ClassDeclaration | InterfaceDeclaration
	 * 
	 * ClassDeclaration: [ Javadoc ] { ExtendedModifier } class Identifier [ <
	 * TypeParameter { , TypeParameter } > ] [ extends Type ] [ implements Type
	 * { , Type } ] { { ClassBodyDeclaration | ; } }
	 * 
	 * InterfaceDeclaration: [ Javadoc ] { ExtendedModifier } interface
	 * Identifier [ < TypeParameter { , TypeParameter } > ] [ extends Type { ,
	 * Type } ] { { InterfaceBodyDeclaration | ; } }
	 * 
	 */
	@Override
	public boolean visit(TypeDeclaration node) {
		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		if (node.isInterface()) {
			addToken(TokenValue.INTERFACE);
		} else {
			addToken(TokenValue.CLASS);
		}
		node.getName().accept(this);
		if (!node.typeParameters().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeParameters(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		if (node.isInterface()) {
			if (!node.superInterfaceTypes().isEmpty()) {
				addToken(TokenValue.EXTENDS);
				visitList(node.superInterfaceTypes(), TokenValue.COMMA);
			}
		} else {
			if (node.getSuperclassType() != null) {
				addToken(TokenValue.EXTENDS);
				node.getSuperclassType().accept(this);
			}
			if (!node.superInterfaceTypes().isEmpty()) {
				addToken(TokenValue.IMPLEMENTS);
				visitList(node.superInterfaceTypes(), TokenValue.COMMA);
			}
		}
		addToken(TokenValue.OPENING_BRACKET);
		if (node.bodyDeclarations().isEmpty()) {
			addToken(TokenValue.SEMICOLON);
		} else {
			visitList(node.bodyDeclarations());
		}
		addToken(TokenValue.CLOSING_BRACKET);

		return false;
	}

	/**
	 * TypeDeclarationStatement: TypeDeclaration | EnumDeclaration
	 */
	@Override
	public boolean visit(TypeDeclarationStatement node) {
		node.getDeclaration().accept(this);
		return false;
	}

	/**
	 * TypeLiteral: ( Type | void ) . class
	 */
	@Override
	public boolean visit(TypeLiteral node) {
		if (node.getType() == null) {
			addToken(TokenValue.VOID);
		} else {
			node.getType().accept(this);
		}
		addToken(TokenValue.DOT);
		addToken(TokenValue.CLASS);

		return false;
	}

	/**
	 * TypeMethodReference: Type :: [ < Type { , Type } > ] Identifier
	 */
	@Override
	public boolean visit(TypeMethodReference node) {
		node.getType().accept(this);
		addToken(TokenValue.DOUBLE_COLON);
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getName().accept(this);

		return false;
	}

	/**
	 * TypeParameter: { ExtendedModifier } Identifier [ extends Type { & Type }
	 * ]
	 */
	@Override
	public boolean visit(TypeParameter node) {
		visitList(node.modifiers());
		node.getName().accept(this);
		if (!node.typeBounds().isEmpty()) {
			addToken(TokenValue.EXTENDS);
			visitList(node.typeBounds(), TokenValue.AMPERSAND);
		}

		return false;
	}

	/**
	 * UnionType: Type | Type { | Type }
	 */
	@Override
	public boolean visit(UnionType node) {
		visitList(node.types(), TokenValue.VERTICAL_BAR);
		return false;
	}

	/**
	 * VariableDeclarationExpression: { ExtendedModifier } Type
	 * VariableDeclarationFragment { , VariableDeclarationFragment }
	 */
	@Override
	public boolean visit(VariableDeclarationExpression node) {
		visitList(node.modifiers());
		node.getType().accept(this);
		visitList(node.fragments(), TokenValue.COMMA);

		return false;
	}

	/**
	 * VariableDeclarationFragment: Identifier { Dimension } [ = Expression ]
	 */
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		node.getName().accept(this);
		visitList(node.extraDimensions());
		if (node.getInitializer() != null) {
			addToken(TokenValue.EQUAL);
			node.getInitializer().accept(this);
		}

		return false;
	}

	/**
	 * VariableDeclarationStatement: { ExtendedModifier } Type
	 * VariableDeclarationFragment { , VariableDeclarationFragment } ;
	 */
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		visitList(node.modifiers());
		node.getType().accept(this);
		visitList(node.fragments(), TokenValue.COMMA);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * WhileStatement: while ( Expression ) Statement
	 */
	@Override
	public boolean visit(WhileStatement node) {
		addToken(TokenValue.WHILE);
		addToken(TokenValue.OPENING_PARENTHESIS);
		node.getExpression().accept(this);
		addToken(TokenValue.CLOSING_PARENTHESIS);
		node.getBody().accept(this);

		return false;
	}

	/**
	 * WildcardType: { Annotation } ? [ ( extends | super) Type ]
	 */
	@Override
	public boolean visit(WildcardType node) {
		visitList(node.annotations());
		addToken(TokenValue.INTERROGATION_MARK);
		if (node.getBound() != null) {
			if (node.isUpperBound()) {
				addToken(TokenValue.EXTENDS);
			} else {
				addToken(TokenValue.SUPER);
			}
		}

		return false;
	}
}
