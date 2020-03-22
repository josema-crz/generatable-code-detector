/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.gst.asttokenizer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.CompilationUnit;

import similaritycalculation.CodeUnit;
import similaritycalculation.CodeUnitType;
import similaritycalculation.gst.Token;
import similaritycalculation.gst.Tokenizer;
import similaritycalculation.helpers.ASTParserHelper;

/**
 * Tokenizer which is based on using the Eclipse AST to create a tokenlist for a
 * java class.
 */
public class EclipseASTTokenizer extends Tokenizer {
	/**
	 * Creates an Eclipse AST Tokenizer.
	 * 
	 * @param configuration
	 *            Configuration values.
	 */
	public EclipseASTTokenizer() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkCU(CodeUnit codeUnit) {
		// An AST has to be built, therefore this tokenizer only support Java
		// code.
		return (codeUnit.getType() == CodeUnitType.JAVA_FILE || codeUnit.getType() == CodeUnitType.JAVA_CLASS
				|| codeUnit.getType() == CodeUnitType.JAVA_METHOD);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkStep(int step) {
		// This tokenizer currently supports the default step and four
		// additional pre-processing steps.
		return (step >= 0 && step <= 4 || step == 10);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Token> tokenize(CodeUnit codeUnit, IType modelClass, int step) {
		CompilationUnit cu = new ASTParserHelper().getCompilationUnitForCodeUnit(codeUnit, true);
		EclipseASTTokenVisitorDefaultStep visitor = null;

		// Select the right visitor according to the step.
		switch (step) {
		case 0:
			visitor = new EclipseASTTokenVisitorDefaultStep(codeUnit, modelClass);
			break;
		case 1:
			visitor = new EclipseASTTokenVisitorStep1(codeUnit, modelClass);
			break;
		case 2:
			visitor = new EclipseASTTokenVisitorStep2(codeUnit, modelClass);
			break;
		case 3:
			visitor = new EclipseASTTokenVisitorStep3(codeUnit, modelClass);
			break;
		case 4:
			visitor = new EclipseASTTokenVisitorStep4(codeUnit, modelClass);
			break;
		case 10:
			EclipseASTTokenVisitorStep10 visitor2 = new EclipseASTTokenVisitorStep10();
			cu.accept(visitor2);
			return visitor2.getTokenList();

		default:
			return new LinkedList<Token>();
		}
		cu.accept(visitor);

		return visitor.getTokenList();
	}
}
