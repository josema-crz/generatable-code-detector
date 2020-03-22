package generationcandidatesdetector.wizards.clonedetection;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import generationcandidatesdetector.controls.FilesTable;
import generationcandidatesdetector.controls.OutputGroup;

/**
 * Page to handle the input and output of the clone detection process.
 */
public class CloneDetectionIOPage extends WizardPage {
	/** Path where the result will be created. */
	private String outputPath;
	/** Table to manage the files. */
	private FilesTable table;
	/** List of initial files selected. */
	private List<IFile> files;

	protected CloneDetectionIOPage(List<IFile> files) {
		super("Input/Output");
		setTitle("Input/Output");
		setDescription("Set the directories and artifacts that will be used during the clone detection process.");

		// List of initial files selected.
		this.files = files;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		/** ---- Output ---- */
		OutputGroup outputGroup = new OutputGroup(container);
		/* When the user selects a directory, it gets set in the Text field. 
		 * Therefore, when this text is modified, we update our attribute with 
		 * the selected directory. */
		outputGroup.getOutputSelec().getDirText().addModifyListener(new ModifyListener() {
			/** {@inheritDoc} */
			public void modifyText(ModifyEvent e) {
				outputPath = outputGroup.getOutputSelec().getDirPath();
				getContainer().updateButtons();
			}
		});
		// So that the focus doesn't go to the text field.
		outputGroup.getOutputLabel().forceFocus();

		/** ---- Input ---- */
		Group inputGroup = new Group(container, SWT.SHADOW_NONE);
		inputGroup.setText("Input");
		GridLayout inputLayout = new GridLayout();
		inputLayout.numColumns = 2;
		inputGroup.setLayout(inputLayout);
		inputGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		table = new FilesTable(inputGroup, files);

		// required to avoid an error in the system
		setControl(container);
	}

	@Override
	public boolean isPageComplete() {
		// Only the output path is necessary for the process.
		return outputPath != null && !outputPath.isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && getErrorMessage() == null;
	}

	/**
	 * Returns the files that will be used, indexed by their simple name.
	 * 
	 * @return A map with the files.
	 */
	public Map<String, IFile> getFiles() {
		return table.getFiles();
	}

	public String getOutputPath() {
		return outputPath;
	}
}
