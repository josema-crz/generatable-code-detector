package generationcandidatesdetector.wizards.similaritycalculation;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import generationcandidatesdetector.controls.CodeUnitsTable;
import generationcandidatesdetector.controls.OutputGroup;
import similaritycalculation.CodeUnit;

/**
 * Wizard page to collect the information about the input and output of the
 * similarity calculation process when calculated between multiple files.
 */
public class SimilarityCalculationXIOPage extends SimilarityCalculationIOPage {
	/** Table to manage the code units. */
	private CodeUnitsTable table;
	/** List of initial code units selected. */
	private List<CodeUnit> codeUnits;

	protected SimilarityCalculationXIOPage(List<CodeUnit> codeUnits) {
		super("Input/Output");
		setTitle("Input/Output");
		setDescription(
				"Set the directories and artifacts that will be used during the similarity calculation process.");

		// List of initial code units selected.
		this.codeUnits = codeUnits;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		/** ---- Output ---- */
		OutputGroup outputGroup = new OutputGroup(container);
		/*
		 * When the user selects a directory, it gets set in the Text field.
		 * Therefore, when this text is modified, we update our attribute with
		 * the selected directory.
		 */
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

		table = new CodeUnitsTable(inputGroup, codeUnits);

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
	 * Returns the code units that will be compared. For each code unit, a
	 * related model class can be specified, or null otherwise.
	 * 
	 * @return A map with the code units and their optional model classes.
	 */
	public Map<CodeUnit, IType> getCodeUnits() {
		return table.getCodeUnits();
	}
}
