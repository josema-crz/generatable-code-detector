package generationcandidatesdetector.handlers;

import java.util.LinkedList;
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
import org.eclipse.ui.handlers.HandlerUtil;

import generationcandidatesdetector.helpers.DialogHelpers;
import generationcandidatesdetector.helpers.SimilarityCalculationHelpers;
import generationcandidatesdetector.wizards.similaritycalculation.SimilarityCalculationXWizard;
import similaritycalculation.CodeUnit;

public class SimilarityCalculationXHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// We get the current selection.
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Object[] selectionsArray = strucSelection.toArray();

			try {
				// We get all the selected code units
				List<CodeUnit> codeUnits = new LinkedList<CodeUnit>();
				List<IFile> files = SimilarityCalculationHelpers.getFilesFromSelections(selectionsArray);
				for (Object file : files) {
					// Get the code unit from each file.
					try {
						codeUnits.add(SimilarityCalculationHelpers.getCodeUnitFromObject(file));
					} catch (UnsupportedOperationException | CoreException e) {
						return new LinkedList<CodeUnit>();
					}
				}
				
				// We show the wizard to collect all the needed information.
				WizardDialog wizardDialog = new WizardDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
						new SimilarityCalculationXWizard(codeUnits));
				if (wizardDialog.open() == Window.OK) {
					// Do nothing
				} else {
					// Do nothing
				}

			} catch (CoreException e) {
				// File could not be read
				DialogHelpers.showErrorDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "File error",
						"The selected file's contents could not be read.");
				dispose();
			} catch (UnsupportedOperationException e) {
				// The type of artifacts selected is not supported
				DialogHelpers.showErrorDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
						"Unsupported operation", "The type of artifact selected is not supported.");
				dispose();
			}
		} else {
			// Incorrect selection
			DialogHelpers.showErrorDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "Incorrect selection",
					"Exactly one file must be selected.");
		}
		return null;
	}

}
