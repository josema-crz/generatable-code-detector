/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.helpers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTParser;

import similaritycalculation.CodeUnit;

/**
 * This class stores all information that are necessary to resolve bindings when
 * creating the Eclipse AST. We currently set the source for the AST creation
 * using {@link ASTParser#setSource(char[])}. In this case, the bindings can
 * only be resolved if, e.g., the project and the unit name are set (see Javadoc
 * for {@link ASTParser#setResolveBindings(boolean)}).
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class ASTBindingInfoManager {
	private static ASTBindingInfoManager manager = new ASTBindingInfoManager();

	private Map<CodeUnit, IJavaProject> codeUnitProjectMap;

	private Map<CodeUnit, String> codeUnitUnitNameMap;

	private ASTBindingInfoManager() {
		this.codeUnitProjectMap = new HashMap<CodeUnit, IJavaProject>();
		this.codeUnitUnitNameMap = new HashMap<CodeUnit, String>();
	}

	public static ASTBindingInfoManager getInstance() {
		if (manager == null) {
			manager = new ASTBindingInfoManager();
		}
		return manager;
	}

	public void addProjectForCodeUnit(CodeUnit codeUnit, IJavaProject project) {
		this.codeUnitProjectMap.put(codeUnit, project);
	}

	public IJavaProject getProjectForCodeUnit(CodeUnit codeUnit) {
		return this.codeUnitProjectMap.get(codeUnit);
	}

	public void addUnitNameForCodeUnit(CodeUnit codeUnit, String unitName) {
		this.codeUnitUnitNameMap.put(codeUnit, unitName);
	}

	public String getUnitNameForCodeUnit(CodeUnit codeUnit) {
		return this.codeUnitUnitNameMap.get(codeUnit);
	}
}
