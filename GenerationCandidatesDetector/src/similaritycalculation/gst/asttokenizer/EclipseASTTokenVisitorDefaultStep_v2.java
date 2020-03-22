/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.gst.asttokenizer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
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
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
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
import org.eclipse.jdt.core.dom.Expression;
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
import org.eclipse.jdt.core.dom.Statement;
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
import org.eclipse.jdt.core.dom.Type;
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

public class EclipseASTTokenVisitorDefaultStep_v2 extends ASTVisitor {
	/**
	 * This list stores the {@link TokenSymbols_Step10} collected when
	 * traversing/visiting the AST.
	 */
	protected List<Token> tokenList;

	/**
	 * The code unit whose content is being visited.
	 */
	protected CodeUnit codeUnit;

	/**
	 * Creates an instance which initializes all fields with empty
	 * datastructures.
	 */
	public EclipseASTTokenVisitorDefaultStep_v2(CodeUnit codeUnit) {
		this.codeUnit = codeUnit;
		this.tokenList = new LinkedList<Token>();
	}

	/**
	 * Returns the list of tokens collected by traversing/visiting the AST.
	 * 
	 * @return See description.
	 */
	public List<Token> getTokenList() {
		return this.tokenList;
	}

	/**
	 * Adds a token to {@link EclipseASTTokenVisitorDefaultStep_v2#tokenList}.
	 * This token refers to the position of the given node and the given symbol.
	 * 
	 * @param node
	 *            AST node for which a token is created.
	 * @param symbol
	 *            {@link TokenSymbols_Step10} representing the given node.
	 */
	protected void addTokenWithValue(final ASTNode node, String value) {
		addTokenWithValue(node.getStartPosition(), node.getStartPosition() + node.getLength(), value);
	}

	protected void addTokenWithValue(int startPos, int endPos, String value) {
		Token token = new Token(codeUnit, startPos, endPos, value);
		this.tokenList.add(token);
	}

	@Override
	public void preVisit(ASTNode node) {
		if (node instanceof Statement) {
			if (node.getParent() instanceof IfStatement) {
				IfStatement parent = (IfStatement) node.getParent();
				if (node.equals(parent.getElseStatement())) {
					// Add the else keyword before the else statement
					addTokenWithValue(node, Identifiers_Step0v2.ELSE);
				}
			} else if (node.getParent() instanceof LabeledStatement) {
				addTokenWithValue(node, Identifiers_Step0v2.LABELED_STATEMENT_SEPARATOR);
			}
		} else if (node instanceof Expression) {
			if (node.getParent() instanceof IfStatement) {
				// The condition of an if statement must be delimited
				addTokenWithValue(node, Identifiers_Step0v2.IF_CONDITION_BEGIN);
			} else if (node.getParent() instanceof ArrayAccess) {
				ArrayAccess parent = (ArrayAccess) node.getParent();
				if (node.equals(parent.getIndex())) {
					// The index of an array access must be delimited
					addTokenWithValue(node, Identifiers_Step0v2.ARRAY_ACCESS_INDEX_BEGIN);
				}
			} else if (node.getParent() instanceof Assignment) {
				Assignment parent = (Assignment) node.getParent();
				if (node.equals(parent.getRightHandSide())) {
					// We add the operator before the right operand of an
					// assignment
					addTokenWithValue(node, parent.getOperator().toString());
				}
			} else if (node.getParent() instanceof ConditionalExpression) {
				ConditionalExpression parent = (ConditionalExpression) node.getParent();
				// A conditional expression has the form Expression ? Expression
				// : Expression
				// We append the structural characters where necessary.
				if (node.equals(parent.getThenExpression())) {
					addTokenWithValue(node, Identifiers_Step0v2.CONDITIONAL_EXPRESSION_CONDITION);
				} else if (node.equals(parent.getElseExpression())) {
					addTokenWithValue(node, Identifiers_Step0v2.CONDITIONAL_EXPRESSION_ELSE);
				}
			} else if (node.getParent() instanceof ConstructorInvocation) {
				ConstructorInvocation parent = (ConstructorInvocation) node.getParent();
				if (node.equals(parent.arguments().get(0))) {
					// Before the first argument, a parenthesis has to be added.
					addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_BEGIN);
				}
			} else if (node.getParent() instanceof DoStatement) {
				// This is the while part of a do-while structure
				addTokenWithValue(node, Identifiers_Step0v2.DO_WHILE_BEGIN);
			} else if (node.getParent() instanceof EnhancedForStatement) {
				// The ':' character goes between the FormalParameter and
				// the Expression in an enhanced for loop.
				addTokenWithValue(node, Identifiers_Step0v2.FOR_ENHANCED_EXPRESSION_BEGIN);
			} else if (node.getParent() instanceof InfixExpression) {
				InfixExpression parent = (InfixExpression) node.getParent();
				// Before the right operand, the operator is appended
				if (node.equals(parent.getRightOperand())) {
					addTokenWithValue(node, parent.getOperator().toString());
				}
			} else if (node.getParent() instanceof MemberValuePair) {
				MemberValuePair parent = (MemberValuePair) node.getParent();
				if (node.equals(parent.getValue())) {
					addTokenWithValue(node, Identifiers_Step0v2.MEMBER_VALUE_PAIR_SEPARATOR);
				}
			} else if (node.getParent() instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration parent = (SingleVariableDeclaration) node.getParent();
				if (node.equals(parent.getInitializer())) {
					addTokenWithValue(node, Identifiers_Step0v2.EQUAL);
				}
			} else if (node.getParent() instanceof SuperMethodInvocation) {
				SuperMethodInvocation parent = (SuperMethodInvocation) node.getParent();
				if (node.equals(parent.arguments().get(0))) {
					// Before the first argument, a parenthesis has to be added.
					addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_BEGIN);
				}
			} else if (node.getParent() instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment parent = (VariableDeclarationFragment) node.getParent();
				if (node.equals(parent.getInitializer())) {
					addTokenWithValue(node, Identifiers_Step0v2.EQUAL);
				}
			}
		} else if (node instanceof Type) {
			if (node.getParent() instanceof CastExpression) {
				// The type of a cast expression must be delimited
				addTokenWithValue(node, Identifiers_Step0v2.CAST_TYPE_BEGIN);
			} else if (node.getParent() instanceof ClassInstanceCreation) {
				// The type must be preceded by the keyword new when creating a
				// new class instance
				addTokenWithValue(node, Identifiers_Step0v2.CLASS_INSTANCE_CREATION_BEGIN);
			} else if (node.getParent() instanceof EnumDeclaration) {
				// This is the type of an interface that the enum being created
				// implements.
				EnumDeclaration parent = (EnumDeclaration) node.getParent();
				if (node.equals(parent.superInterfaceTypes().get(0))) {
					// Before the first interface, the keyword "implements" is
					// added.
					addTokenWithValue(node, Identifiers_Step0v2.IMPLEMENTS);
				}
			} else if (node.getParent() instanceof InstanceofExpression) {
				addTokenWithValue(node, Identifiers_Step0v2.INSTANCEOF);
			} else if (node.getParent() instanceof UnionType) {
				// The types in a UnionType are separated
				UnionType parent = (UnionType) node.getParent();
				if (!node.equals(parent.types().get(0))) {
					addTokenWithValue(node, Identifiers_Step0v2.UNION_TYPE_SEPARATOR);
				}
			} else if (node.getParent() instanceof TypeParameter) {
				// The types extended by the TypeParameter being declared
				TypeParameter parent = (TypeParameter) node.getParent();
				if (node.equals(parent.typeBounds().get(0))) {
					addTokenWithValue(node, Identifiers_Step0v2.EXTENDS);
				} else {
					addTokenWithValue(node, Identifiers_Step0v2.TYPE_PARAMETER_SEPARATOR);
				}
			} else if (node.getParent() instanceof MethodDeclaration) {
				// The exception types thrown by the method being declared
				MethodDeclaration parent = (MethodDeclaration) node.getParent();
				if (parent.thrownExceptionTypes().contains(node)) {
					if (node.equals(parent.thrownExceptionTypes().get(0))) {
						addTokenWithValue(node, Identifiers_Step0v2.THROWS);
					} else {
						addTokenWithValue(node, Identifiers_Step0v2.METHOD_THROWS_SEPARATOR);
					}
				}
			}
		}
	}

	@Override
	public void postVisit(ASTNode node) {
		if (node instanceof Expression) {
			if (node.getParent() instanceof IfStatement) {
				// The condition of an if statement must be delimited
				addTokenWithValue(node, Identifiers_Step0v2.IF_CONDITION_END);
			} else if (node.getParent() instanceof ArrayAccess) {
				ArrayAccess parent = (ArrayAccess) node.getParent();
				if (node.equals(parent.getIndex())) {
					// The index of an array access must be delimited
					addTokenWithValue(node, Identifiers_Step0v2.ARRAY_ACCESS_INDEX_END);
				}
			} else if (node.getParent() instanceof ClassInstanceCreation) {
				ClassInstanceCreation parent = (ClassInstanceCreation) node.getParent();
				if (parent.arguments().contains(node)) {
					if (node.equals(parent.arguments().get(parent.arguments().size() - 1))) {
						// After the last argument, a closing parenthesis is
						// added
						addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_END);
					} else {
						// After the rest, a comma is added.
						addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_SEPARATOR);
					}
				}
			} else if (node.getParent() instanceof ConstructorInvocation) {
				ConstructorInvocation parent = (ConstructorInvocation) node.getParent();
				if (parent.arguments().contains(node)) {
					if (node.equals(parent.arguments().get(parent.arguments().size() - 1))) {
						// After the last argument, a closing parenthesis is
						// added
						addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_END);
					} else {
						// After the rest, a comma is added.
						addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_SEPARATOR);
					}
				}
			} else if (node.getParent() instanceof DoStatement) {
				// This is the while part of a do-while structure
				addTokenWithValue(node, Identifiers_Step0v2.DO_WHILE_END);
			} else if (node.getParent() instanceof EnhancedForStatement) {
				// The ':' character goes between the FormalParameter and
				// the Expression in an enhanced for loop.
				addTokenWithValue(node, Identifiers_Step0v2.FOR_ENHANCED_EXPRESSION_END);
			} else if (node.getParent() instanceof ForStatement) {
				ForStatement parent = (ForStatement) node.getParent();
				List initializers = (List) parent.getStructuralProperty(ForStatement.INITIALIZERS_PROPERTY);
				List updaters = (List) parent.getStructuralProperty(ForStatement.UPDATERS_PROPERTY);
				if (initializers.contains(node)) {
					// The last one ends with a semicolon.
					// If there is no expression another semicolon is added.
					// If there are also no updaters the ending parenthesis is
					// added.
					if (node.equals(initializers.get(initializers.size() - 1))) {
						addTokenWithValue(node, Identifiers_Step0v2.FOR_EXPRESSION_SEPARATOR);
						if (parent.getExpression() == null) {
							addTokenWithValue(node, Identifiers_Step0v2.FOR_EXPRESSION_SEPARATOR);
							if (updaters.isEmpty()) {
								addTokenWithValue(node, Identifiers_Step0v2.FOR_EXPRESSION_END);
							}
						}
					} else {
						addTokenWithValue(node, Identifiers_Step0v2.FOR_EXPRESSION_INNER_SEPARATOR);
					}
				} else if (updaters.contains(node)) {
					// The last one ends with a closing parenthesis.
					if (node.equals(initializers.get(initializers.size() - 1))) {
						addTokenWithValue(node, Identifiers_Step0v2.FOR_EXPRESSION_END);
					} else {
						addTokenWithValue(node, Identifiers_Step0v2.FOR_EXPRESSION_INNER_SEPARATOR);
					}
				} else if (node.equals(parent.getExpression())) {
					// It ends with a semicolon.
					addTokenWithValue(node, Identifiers_Step0v2.FOR_EXPRESSION_SEPARATOR);
					// If there are no updaters the ending parenthesis is added.
					if (updaters.isEmpty()) {
						addTokenWithValue(node, Identifiers_Step0v2.FOR_EXPRESSION_END);
					}
				}
			} else if (node.getParent() instanceof SuperMethodInvocation) {
				SuperMethodInvocation parent = (SuperMethodInvocation) node.getParent();
				if (parent.arguments().contains(node)) {
					if (node.equals(parent.arguments().get(parent.arguments().size() - 1))) {
						// After the last argument, a closing parenthesis is
						// added
						addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_END);
					} else {
						// After the rest, a comma is added.
						addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_SEPARATOR);
					}
				}
			} else if (node.getParent() instanceof SynchronizedStatement) {
				addTokenWithValue(node, Identifiers_Step0v2.SYNCHRONIZED_END);
			} else if (node.getParent() instanceof WhileStatement) {
				addTokenWithValue(node, Identifiers_Step0v2.WHILE_END);
			}
		} else if (node instanceof Type) {
			if (node.getParent() instanceof CastExpression) {
				// The type of a cast expression must be delimited
				addTokenWithValue(node, Identifiers_Step0v2.CAST_TYPE_END);
			} else if (node.getParent() instanceof EnumDeclaration) {
				// This is the type of an interface that the enum being created
				// implements.
				EnumDeclaration parent = (EnumDeclaration) node.getParent();
				if (parent.superInterfaceTypes().contains(node)) {
					if (node.equals(parent.superInterfaceTypes().get(parent.superInterfaceTypes().size() - 1))) {
						// After the last interface, an opening bracket is
						// added
						addTokenWithValue(node, Identifiers_Step0v2.ENUM_DECLARATION_BEGIN);
					} else {
						// After the rest, a comma is added.
						addTokenWithValue(node, Identifiers_Step0v2.IMPLEMENTS_SEPARATOR);
					}
				}
			} else if (node.getParent() instanceof ParameterizedType) {
				// The type arguments are comma separated and between <>
				ParameterizedType parent = (ParameterizedType) node.getParent();
				if (parent.typeArguments().contains(node)) {
					if (node.equals(parent.typeArguments().get(parent.typeArguments().size() - 1))) {
						addTokenWithValue(node, Identifiers_Step0v2.PARAMETERIZED_TYPE_END);
					} else {
						addTokenWithValue(node, Identifiers_Step0v2.PARAMETERIZED_TYPE_SEPARATOR);
					}
				} else {
					addTokenWithValue(node, Identifiers_Step0v2.PARAMETERIZED_TYPE_BEGIN);
				}
			} else if (node.getParent() instanceof QualifiedType) {
				addTokenWithValue(node, Identifiers_Step0v2.QUALIFIED_TYPE_SEPARATOR);
			} else if (node.getParent() instanceof ClassInstanceCreation) {
				// An opening parenthesis goes after the type
				ClassInstanceCreation parent = (ClassInstanceCreation) node.getParent();
				addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_BEGIN);
				if (parent.arguments().isEmpty()) {
					// If there are no arguments, the closing parenthesis is
					// also added
					addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_END);
				}
			}
		}
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		addTokenWithValue(node, Identifiers_Step0v2.ANONYMOUS_CLASS_DECLARATION_BEGIN);
		return false;
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		addTokenWithValue(node, Identifiers_Step0v2.ANONYMOUS_CLASS_DECLARATION_END);
	}

	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		addTokenWithValue(node, Identifiers_Step0v2.ARRAY_CREATION);
		return true;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		addTokenWithValue(node, Identifiers_Step0v2.ARRAY_INITIALIZER_BEGIN);
		return true;
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		addTokenWithValue(node, Identifiers_Step0v2.ARRAY_INITIALIZER_END);
	}

	@Override
	public boolean visit(ArrayType node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(AssertStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.ASSERT_STATEMENT);
		return true;
	}

	@Override
	public boolean visit(Assignment node) {
		return true;
	}

	@Override
	public boolean visit(Block node) {
		addTokenWithValue(node, Identifiers_Step0v2.BLOCK_BEGIN);
		return true;
	}

	@Override
	public void endVisit(Block node) {
		addTokenWithValue(node, Identifiers_Step0v2.BLOCK_END);
	}

	@Override
	public boolean visit(BlockComment node) {
		// TODO Test to see if the /* */ are added or not
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.BREAK);
		return false;
	}

	@Override
	public boolean visit(CastExpression node) {
		return true;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		addTokenWithValue(node, Identifiers_Step0v2.CONSTRUCTOR_INVOCATION);
		return true;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.CONTINUE);
		return false;
	}

	@Override
	public void endVisit(ContinueStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
	}

	@Override
	public boolean visit(CreationReference node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(Dimension node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(DoStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.DO);
		return true;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.FOR_ENHANCED_BEGIN);
		return true;
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		// TODO According to the documentation, the declaration of an enum
		// constant can be much more complex than I thought. I decide to just
		// let the child nodes be processed. Usually, there will only be a
		// SimpleName.
		return true;
	}

	@Override
	public void endVisit(EnumConstantDeclaration node) {
		EnumDeclaration parent = (EnumDeclaration) node.getParent();
		// The constants are comma separated, and the block ends with a
		// semicolon
		if (node.equals(parent.enumConstants().get(parent.enumConstants().size() - 1))) {
			addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
		} else {
			addTokenWithValue(node, Identifiers_Step0v2.ENUM_CONSTANT_DECLARATION_SEPARATOR);
		}
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return true;
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		addTokenWithValue(node, Identifiers_Step0v2.ENUM_DECLARATION_END);
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		return true;
	}

	@Override
	public void endVisit(ExpressionStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
	}

	@Override
	public boolean visit(FieldAccess node) {
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		return true;
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
	}

	@Override
	public boolean visit(ForStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.FOR_BEGIN);
		return true;
	}

	@Override
	public boolean visit(IfStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.IF);
		return true;
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		// We directly print it all, it is not an important node anyway.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(InfixExpression node) {
		return true;
	}

	@Override
	public boolean visit(Initializer node) {
		return true;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		return true;
	}

	@Override
	public boolean visit(IntersectionType node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(Javadoc node) {
		// Check the format of this
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(LambdaExpression node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(LineComment node) {
		// Check the format, if the slashes are added or not
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		// We directly print it all, it is not an important node anyway.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(MemberRef node) {
		return true;
	}

	@Override
	public boolean visit(MemberValuePair node) {
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		return true;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		if (node.getBody() == null) {
			addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
		}
	}

	public void visit(Type node) {
		System.out.println("");
	}

	@Override
	public boolean visit(MethodInvocation node) {
		// TODO
		return true;
	}

	@Override
	public boolean visit(MethodRef node) {
		return true;
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		return true;
	}

	@Override
	public boolean visit(Modifier node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(NameQualifiedType node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		// We directly print it all, it is not an important node anyway.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(NullLiteral node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		// We directly print it all, it is not an important node anyway.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(ParameterizedType node) {
		return true;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		addTokenWithValue(node, Identifiers_Step0v2.PARENTHESIS_BEGIN);
		return true;
	}

	@Override
	public void endVisit(ParenthesizedExpression node) {
		addTokenWithValue(node, Identifiers_Step0v2.PARENTHESIS_END);
	}

	@Override
	public boolean visit(PostfixExpression node) {
		return true;
	}

	@Override
	public void endVisit(PostfixExpression node) {
		addTokenWithValue(node, node.getOperator().toString());
	}

	@Override
	public boolean visit(PrefixExpression node) {
		addTokenWithValue(node, node.getOperator().toString());
		return true;
	}

	@Override
	public boolean visit(PrimitiveType node) {
		addTokenWithValue(node, node.getPrimitiveTypeCode().toString());
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		return true;
	}

	@Override
	public boolean visit(QualifiedType node) {
		return true;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.RETURN);
		return true;
	}

	@Override
	public void endVisit(ReturnStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
	}

	@Override
	public boolean visit(SimpleName node) {
		if (node.getParent() instanceof EnumDeclaration) {
			// This is the name of an enum that is being created now.
			addTokenWithValue(node, Identifiers_Step0v2.ENUM_DECLARATION);
		} else if (node.getParent() instanceof FieldAccess) {
			// This is the name of a field that is being accessed.
			addTokenWithValue(node, Identifiers_Step0v2.FIELD_ACCESS);
		} else if (node.getParent() instanceof QualifiedName) {
			addTokenWithValue(node, Identifiers_Step0v2.QUALIFIED_NAME_SEPARATOR);
		}

		addTokenWithValue(node, node.getIdentifier());
		return false;
	}

	@Override
	public void endVisit(SimpleName node) {
		if (node.getParent() instanceof EnumDeclaration) {
			EnumDeclaration parent = (EnumDeclaration) node.getParent();
			if (parent.superInterfaceTypes().isEmpty()) {
				// The enum doesn´t implement any interface so its body
				// declaration starts now.
				addTokenWithValue(node, Identifiers_Step0v2.ENUM_DECLARATION_BEGIN);
			}
		} else if (node.getParent() instanceof MethodDeclaration) {
			MethodDeclaration parent = (MethodDeclaration) node.getParent();
			// After a method's name there is always an opening parenthesis.
			addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_BEGIN);
			if (parent.parameters().isEmpty()) {
				// If the method has no parameters, the closing parenthesis is
				// also added.
				addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_END);
			}
		}
	}

	@Override
	public boolean visit(SimpleType node) {
		addTokenWithValue(node, node.getName().toString());
		return false;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		// We directly print it all, it is not an important node anyway.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		return true;
	}

	@Override
	public void endVisit(SingleVariableDeclaration node) {
		if (node.getParent() instanceof MethodDeclaration) {
			// The parameters in a method declaration are comma separated and
			// between parenthesis
			MethodDeclaration parent = (MethodDeclaration) node.getParent();
			if (node.equals(parent.parameters().get(parent.parameters().size() - 1))) {
				addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_END);
			} else {
				addTokenWithValue(node, Identifiers_Step0v2.ARGUMENTS_SEPARATOR);
			}
		}
	}

	@Override
	public boolean visit(StringLiteral node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		// TODO This is actually more complex, has been simplified to the most
		// common case.
		addTokenWithValue(node, Identifiers_Step0v2.SUPER_CONSTRUCTOR_BEGIN);
		return true;
	}

	@Override
	public void endVisit(SuperConstructorInvocation node) {
		addTokenWithValue(node, Identifiers_Step0v2.SUPER_CONSTRUCTOR_END);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		// TODO There is also a non-common detail here that has been omitted.
		addTokenWithValue(node, Identifiers_Step0v2.SUPER_FIELD_BEGIN);
		return true;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		// TODO This is actually more complex, has been simplified to the most
		// common case.
		addTokenWithValue(node, Identifiers_Step0v2.SUPER_METHOD_BEGIN);
		return true;
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(SwitchCase node) {
		// TODO
		return true;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		// TODO
		return true;
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.SYNCHRONIZED_BEGIN);
		return true;
	}

	@Override
	public boolean visit(TagElement node) {
		// We directly print it all, it is not an important node anyway.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(TextElement node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(ThisExpression node) {
		return true;
	}

	@Override
	public void endVisit(ThisExpression node) {
		if (node.getQualifier() != null) {
			addTokenWithValue(node, Identifiers_Step0v2.THIS_SEPARATOR);
		}
		addTokenWithValue(node, Identifiers_Step0v2.THIS);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.THROW);
		return true;
	}

	@Override
	public void endVisit(ThrowStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
	}

	@Override
	public boolean visit(TryStatement node) {
		// TODO
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		// TODO
		return true;
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		return true;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(TypeMethodReference node) {
		// TODO Still not supported, so we just print it.
		addTokenWithValue(node, node.toString());
		return false;
	}

	@Override
	public boolean visit(TypeParameter node) {
		if (node.getParent() instanceof MethodDeclaration) {
			// Type parameters in a method declaration are comma separated and
			// between <>
			MethodDeclaration parent = (MethodDeclaration) node.getParent();
			if (node.equals(parent.typeParameters().get(0))) {
				addTokenWithValue(node, Identifiers_Step0v2.METHOD_TYPE_PARAMETERS_BEGIN);
			} else {
				addTokenWithValue(node, Identifiers_Step0v2.METHOD_TYPE_PARAMETERS_SEPARATOR);
			}
		}

		return true;
	}

	@Override
	public void endVisit(TypeParameter node) {
		if (node.getParent() instanceof MethodDeclaration) {
			// Type parameters in a method declaration are comma separated and
			// between <>
			MethodDeclaration parent = (MethodDeclaration) node.getParent();
			if (node.equals(parent.typeParameters().get(parent.typeParameters().size() - 1))) {
				addTokenWithValue(node, Identifiers_Step0v2.METHOD_TYPE_PARAMETERS_END);
			}
		}
	}

	@Override
	public boolean visit(UnionType node) {
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		return true;
	}

	@Override
	public void endVisit(VariableDeclarationFragment node) {
		if (node.getParent() instanceof FieldDeclaration) {
			FieldDeclaration parent = (FieldDeclaration) node.getParent();
			if (!node.equals(parent.fragments().get(parent.fragments().size() - 1))) {
				// The variables declared inside a field declaration need to be
				// separated.
				addTokenWithValue(node, Identifiers_Step0v2.FIELD_DECLARATION_SEPARATOR);
			}
		} else if (node.getParent() instanceof VariableDeclarationExpression) {
			VariableDeclarationExpression parent = (VariableDeclarationExpression) node.getParent();
			if (!node.equals(parent.fragments().get(parent.fragments().size() - 1))) {
				// The variables declared inside a variable declaration need to
				// be separated.
				addTokenWithValue(node, Identifiers_Step0v2.VARIABLE_DECLARATION_SEPARATOR);
			}
		} else if (node.getParent() instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement parent = (VariableDeclarationStatement) node.getParent();
			if (!node.equals(parent.fragments().get(parent.fragments().size() - 1))) {
				// The variables declared inside a variable declaration need to
				// be separated.
				addTokenWithValue(node, Identifiers_Step0v2.VARIABLE_DECLARATION_SEPARATOR);
			}
		}
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		return true;
	}

	@Override
	public void endVisit(VariableDeclarationStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.STATEMENT_END);
	}

	@Override
	public boolean visit(WhileStatement node) {
		addTokenWithValue(node, Identifiers_Step0v2.WHILE_BEGIN);
		return true;
	}

	@Override
	public boolean visit(WildcardType node) {
		// TODO The Annotation, if exists, would not be in the right position
		addTokenWithValue(node, Identifiers_Step0v2.WILDCARD);
		if (node.getBound() != null) {
			if (node.isUpperBound()) {
				addTokenWithValue(node, Identifiers_Step0v2.EXTENDS);
			} else {
				addTokenWithValue(node, Identifiers_Step0v2.SUPER);
			}
		}
		return true;
	}

}
