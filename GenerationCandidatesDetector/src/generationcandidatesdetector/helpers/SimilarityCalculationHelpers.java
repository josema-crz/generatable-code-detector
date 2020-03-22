package generationcandidatesdetector.helpers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

import similaritycalculation.CodeUnit;
import similaritycalculation.CodeUnitType;
import similaritycalculation.helpers.ASTBindingInfoManager;
import utils.FileUtils;

/**
 * Helper methods for the similarity calculation handlers.
 */
public class SimilarityCalculationHelpers {
	/**
	 * Creates the corresponding code unit from an object.
	 * 
	 * @param element
	 *            The element.
	 * @return The corresponding code unit.
	 * @throws CoreException
	 *             When there is an error reading the contents of the element.
	 * @throws UnsupportedOperationException
	 *             When the element selected is not supported.
	 */
	public static CodeUnit getCodeUnitFromObject(Object element) throws CoreException, UnsupportedOperationException {
		CodeUnit codeUnit = null;

		// Check if it is a File
		if (element instanceof IFile) {
			IFile file = (IFile) element;

			// Try first to turn the file into a compilation unit.
			if (file.getFileExtension() != null && file.getFileExtension().equals("java")) {
				element = JavaCore.createCompilationUnitFrom(file);
			} else {
				// Create the code unit for the file.
				codeUnit = new CodeUnit(file.getFullPath().toString(), FileUtils.getSimpleName(file),
						file.getContents().toString(), CodeUnitType.FILE);

				return codeUnit;
			}
		}
		// Check if it is a Compilation Unit
		if (element instanceof ICompilationUnit) {
			// Create the code unit for the compilation unit
			ICompilationUnit cu = (ICompilationUnit) element;
			codeUnit = new CodeUnit(cu.getPath().toString(), FileUtils.getSimpleName(cu.getElementName()),
					cu.getSource(), CodeUnitType.JAVA_FILE);

			// TODO This should be better handled, since we already have the
			// Compilation Unit and is not a good approach having it
			// converted to a CodeUnit only to later obtain again a
			// CompilationUnit.

			// Set the info for the correct calculation of the binding
			ASTBindingInfoManager bindingManager = ASTBindingInfoManager.getInstance();
			bindingManager.addProjectForCodeUnit(codeUnit, cu.getJavaProject());
			bindingManager.addUnitNameForCodeUnit(codeUnit, cu.getElementName());

		} else {
			// Element not supported
			throw new UnsupportedOperationException();
		}
		return codeUnit;
	}

	/**
	 * Get the list of files contained in the selected elements.
	 * 
	 * @param selectionsArray
	 *            The array with the selected elements.
	 * @return The list of files contained in the selected elements.
	 * @throws CoreException
	 *             When the contents of an IContainer selected element can not
	 *             be read.
	 */
	public static List<IFile> getFilesFromSelections(Object[] selectionsArray) throws CoreException {
		List<IFile> selectedFiles = new LinkedList<IFile>();

		for (Object selected : selectionsArray) {
			// We need to get the Resource first.
			IResource resource = null;
			if (selected instanceof IResource) {
				resource = (IResource) selected;
			} else if (selected instanceof IAdaptable) {
				IAdaptable a = (IAdaptable) selected;
				resource = (IResource) a.getAdapter(IResource.class);
			}

			if (resource != null) {
				// Now we get the file selected or the files contained in
				// the selection, in case a container (folder or project,
				// for example) has been selected.
				if (resource instanceof IFile) {
					// TODO Best place to put this here? Configurable?
					// Ignore .class files, which are always useless.
					if (((IFile) resource).getFileExtension() == null
							|| !((IFile) resource).getFileExtension().equals("class")) {
						selectedFiles.add((IFile) resource);
					}
				} else if (resource instanceof IContainer) {
					selectedFiles.addAll(getFilesFromSelections(((IContainer) resource).members()));
				}
			}
		}

		return selectedFiles;
	}

	/**
	 * Gets the java Type defined in the file.
	 * 
	 * @param typeFile
	 *            The file containing the definition of the type.
	 * @return The Type or null if the file didn't define a type or if there was
	 *         an error.
	 */
	public static IType getTypeFromFile(IFile typeFile) {
		try {
			if (typeFile.getFileExtension() != null && typeFile.getFileExtension().equals("java")) {
				// TODO Check how this method works in opposition to how we
				// usually create the CUs in our process.
				ICompilationUnit classCU = JavaCore.createCompilationUnitFrom(typeFile);

				if (classCU != null && classCU.getTypes().length >= 1) {
					// We take the first type defined in the file.
					IType theClass = classCU.getTypes()[0];

					return theClass;
				}
			}
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
}
