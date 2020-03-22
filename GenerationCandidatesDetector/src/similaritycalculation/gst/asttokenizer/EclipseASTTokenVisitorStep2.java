package similaritycalculation.gst.asttokenizer;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import similaritycalculation.CodeUnit;
import similaritycalculation.gst.TokenValue;

/**
 * Visitor representing the functionality of the second pre-processing step in
 * the global similarity calculation process. It is a generation-specific step
 * where a model class related to the code unit is necessary for the actions.
 * Some basic replacements related to this class will be performed.
 */
public class EclipseASTTokenVisitorStep2 extends EclipseASTTokenVisitorStep1 {
	/** Name of the model class related to the code unit. */
	private String modelClassName;

	/** Names of the fields of the model class related to the code unit. */
	private List<String> modelClassFieldNames;

	/**
	 * Creates a visitor for the second step. Extracts the names that will be
	 * used for the generation-specific replacements from the model class.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @param modelClass
	 *            Model class related to the code unit. It can be null, but some
	 *            pre-processing actions may not be effective depending on the
	 *            step.
	 */
	public EclipseASTTokenVisitorStep2(CodeUnit codeUnit, IType modelClass) {
		super(codeUnit, modelClass);
		if (modelClass != null) {
			// Get the names used for the replacements.
			this.modelClassName = this.modelClass.getElementName();
			this.modelClassFieldNames = getFieldNames(this.modelClass);
		} else {
			// Default values. The visitor will be useless without this
			// information.
			this.modelClassName = null;
			this.modelClassFieldNames = new LinkedList<String>();
		}
		// We sort the names by its length, so that always the longest match is
		// replaced.
		this.modelClassFieldNames.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return Integer.compare(o1.length(), o2.length());
			}
		});
	}

	/**
	 * Returns the names of all the fields of the given class.
	 * 
	 * @param theClass
	 *            The class.
	 * @return A list with the names of the fields of the class.
	 */
	private List<String> getFieldNames(IType theClass) {
		List<String> names = new LinkedList<String>();
		try {
			for (IField field : theClass.getFields()) {
				names.add(field.getElementName());
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return names;
	}

	/**
	 * Replace the model class name and its fields' names in the names of the
	 * methods and types. The names are overwritten and will be treated by the
	 * parent visitors.
	 */
	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();

		if (binding != null) {
			switch (binding.getKind()) {
			case IBinding.METHOD:
				node.setIdentifier(doReplacement(node.getIdentifier()));
			case IBinding.TYPE:
				if (!(node.getParent() instanceof TypeDeclaration)) {
					node.setIdentifier(doReplacement(node.getIdentifier()));
				}
				break;
			default:
				return super.visit(node);
			}
		}
		return super.visit(node);
	}

	/**
	 * Replace the model class name and its fields' names in the string
	 * literals.
	 */
	@Override
	public boolean visit(StringLiteral node) {
		node.setLiteralValue(doReplacement(node.getLiteralValue()));
		return super.visit(node);
	}

	/**
	 * Gets the resulting string after attempting to replace the model class
	 * name and the model fields names in it.
	 * 
	 * @param str
	 * @return
	 */
	private String doReplacement(String str) {
		/* Use regular expressions to make the process case insensitive. */

		// Model class name replacement
		if (modelClassName != null) {
			str = str.replaceAll("(?i)" + Pattern.quote(modelClassName), TokenValue.MODEL_CLASS_NAME.toString());
		}

		// Model fields names replacement
		for (String attrName : modelClassFieldNames) {
			str = str.replaceAll("(?i)" + Pattern.quote(attrName), TokenValue.MODEL_FIELD_NAME.toString());
		}

		return str;
	}
}
