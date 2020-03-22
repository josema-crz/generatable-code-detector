/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import similaritycalculation.CodeUnit;
import similaritycalculation.CodeUnitType;

public class ASTParserHelper {
	// TODO Implement Singleton pattern.

	/**
	 * Returns the {@link CompilationUnit} for a particular code unit. For this
	 * compilation unit, bindings can be resolved, depending on the value of the
	 * parameter <code>resolveBindings</code>.
	 *
	 * @param codeUnit
	 *            The code unit.
	 * @param resolveBindings
	 *            Indicates whether bindings should be resolved for the
	 *            compilation unit to be returned.
	 * @return Compilation unit for file.
	 * @throws IOException
	 */
	public CompilationUnit getCompilationUnitForCodeUnit(CodeUnit codeUnit, boolean resolveBindings) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(getASTParserKind(codeUnit.getType()));
		parser.setSource(codeUnit.getContent().toCharArray());
		if (resolveBindings) {
			ASTBindingInfoManager bindingManager = ASTBindingInfoManager.getInstance();
			IJavaProject proj = bindingManager.getProjectForCodeUnit(codeUnit);
			if (proj != null) {
				parser.setProject(proj);
			}
			String unitName = bindingManager.getUnitNameForCodeUnit(codeUnit);
			if (unitName != null) {
				parser.setUnitName(unitName);
			}

			parser.setResolveBindings(true);
		}

		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		return cu;
	}

	/**
	 * Returns the ASTParser kind corresponding to the CodeUnitType.
	 * 
	 * @param type
	 *            CodeUnitType.
	 * @return The corresponding ASTParser kind.
	 */
	private int getASTParserKind(CodeUnitType type) {
		if (type == CodeUnitType.JAVA_FILE || type == CodeUnitType.JAVA_CLASS) {
			return ASTParser.K_COMPILATION_UNIT;
		}
		if (type == CodeUnitType.JAVA_METHOD) {
			return ASTParser.K_STATEMENTS;
		}
		// Default, it should never be returned.
		return 0;
	}

	/**
	 * Returns the string representing the content of the given file.
	 * 
	 * @param file
	 *            File whose content is returned.
	 * @return File content as string.
	 * @throws IOException
	 *             Thrown if it is not possible to read the given file.
	 */
	public String getFileContent(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder fileContent = new StringBuilder();
		String line;

		while ((line = br.readLine()) != null) {
			fileContent.append(line);
			fileContent.append("\n");
		}

		br.close();
		return fileContent.toString();
	}
}
