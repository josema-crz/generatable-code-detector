package generationcandidatesdetector.helpers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import generationcandidatesdetector.Activator;
import similaritycalculation.CodeUnit;

/**
 * Helper methods related to the creation of dialogs.
 */
public class DialogHelpers {
	/**
	 * Shows a dialog to communicate an error to the user.
	 * 
	 * @param shell
	 *            The parent shell.
	 * @param text
	 *            The title of the dialog.
	 * @param message
	 *            The message of the dialog.
	 */
	public static void showErrorDialog(Shell shell, String text, String message) {
		MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		dialog.setText(text);
		dialog.setMessage(message);
		dialog.open();
	}

	/**
	 * Shows a dialog to communicate some general information to the user.
	 * 
	 * @param shell
	 *            The parent shell.
	 * @param text
	 *            The title of the dialog.
	 * @param message
	 *            The message of the dialog.
	 */
	public static void showInformationDialog(Shell shell, String text, String message) {
		MessageBox dialog = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		dialog.setText(text);
		dialog.setMessage(message);
		dialog.open();
	}

	/**
	 * Shows a dialog asking the user for a Java file specifying a type, and
	 * returns that type.
	 * 
	 * @param shell
	 *            The parent shell.
	 * @return The type defined in the file selected by the user.
	 */
	public static IType getTypeDialog(Shell shell) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());

		dialog.setAllowMultiple(false);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setValidator(new ISelectionStatusValidator() {

			@Override
			public IStatus validate(Object[] selection) {
				if (selection.length == 1 && selection[0] instanceof IFile
						&& checkFileExtension((IFile) selection[0])) {
					return new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
							"Valid selection.");
				}
				return new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
						"Invalid selection. A Java file must be selected.");
			}

			private boolean checkFileExtension(IFile file) {
				return file.getFileExtension().equals("java");
			}
		});
		dialog.setTitle("Type selection");
		dialog.setMessage("Select a file defining a Java type");
		dialog.open();
		IFile typeFile = (IFile) dialog.getResult()[0];

		return SimilarityCalculationHelpers.getTypeFromFile(typeFile);
	}

	/**
	 * Shows a dialog asking the user for files that represent a code unit. A
	 * container can also be selected and the contained code units will be
	 * returned.
	 * 
	 * @param shell
	 *            The parent shell.
	 * @return All the code units selected by the user.
	 */
	public static List<CodeUnit> getCodeUnitsDialog(Shell shell) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());

		dialog.setAllowMultiple(true);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setTitle("Code unit selection");
		dialog.setMessage("Select one or more files that represent a code unit");
		dialog.open();

		List<CodeUnit> codeUnits = new LinkedList<CodeUnit>();

		try {
			List<IFile> files = SimilarityCalculationHelpers.getFilesFromSelections(dialog.getResult());
			for (Object file : files) {
				// Get the code unit from each file.
				try {
					codeUnits.add(SimilarityCalculationHelpers.getCodeUnitFromObject(file));
				} catch (UnsupportedOperationException | CoreException e) {
					return new LinkedList<CodeUnit>();
				}
			}
		} catch (CoreException e1) {
			return new LinkedList<CodeUnit>();
		}
		return codeUnits;
	}

	/**
	 * Shows a dialog asking the user for files. A container can also be
	 * selected and the contained files will be returned.
	 * 
	 * @param shell
	 *            The parent shell.
	 * @return All the files selected by the user.
	 */
	public static List<IFile> getFilesDialog(Shell shell) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());

		dialog.setAllowMultiple(true);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setTitle("File selection");
		dialog.setMessage("Select one or more files");
		dialog.open();
		
		try {
			List<IFile> files = SimilarityCalculationHelpers.getFilesFromSelections(dialog.getResult());
			return files;
		} catch (CoreException e1) {
			return new LinkedList<IFile>();
		}
	}
}
