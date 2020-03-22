package generationcandidatesdetector.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.handlers.HandlerUtil;

import generationcandidatesdetector.helpers.SimilarityCalculationHelpers;
import generationcandidatesdetector.wizards.namesimilarity.NameSimilarityWizard;

/**
 * Handler for the name similarity feature application.
 */
public class NameSimilarityHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// We get the current selection.
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Object[] selectionsArray = strucSelection.toArray();

			try {
				// We obtain the selected files.
				List<IFile> selectedFiles = SimilarityCalculationHelpers.getFilesFromSelections(selectionsArray);

				// We show the wizard to collect all the needed information.
				WizardDialog wizardDialog = new WizardDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
						new NameSimilarityWizard(selectedFiles));
				if (wizardDialog.open() == Window.OK) {
					// Do nothing
				} else {
					// Do nothing
				}

				return null;

			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		// Incorrect selection
		MessageBox dialog = new MessageBox(HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
				SWT.ICON_ERROR | SWT.OK);
		dialog.setText("Incorrect selection");
		dialog.setMessage("The selection is not correct or could not be handled.");
		dialog.open();

		return null;
	}
}
