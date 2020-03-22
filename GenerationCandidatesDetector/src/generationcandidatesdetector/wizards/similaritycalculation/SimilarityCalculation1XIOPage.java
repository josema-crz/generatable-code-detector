package generationcandidatesdetector.wizards.similaritycalculation;

import java.util.Map;

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import generationcandidatesdetector.controls.CodeUnitsTable;
import generationcandidatesdetector.controls.OutputGroup;
import generationcandidatesdetector.controls.TypeSelector;
import similaritycalculation.CodeUnit;

/**
 * Wizard page to collect the information about the input and output of the
 * similarity calculation process when calculated between one file and multiple
 * other files.
 */
public class SimilarityCalculation1XIOPage extends SimilarityCalculationIOPage {
	/** Main code unit being compared. */
	private CodeUnit codeUnit;
	/** Model class related to the main code unit being compared. */
	private IType modelClass;
	/** Table to manage the other code units. */
	private CodeUnitsTable table;

	protected SimilarityCalculation1XIOPage(CodeUnit codeUnit) {
		super("Input/Output");
		setTitle("Input/Output");
		setDescription(
				"Set the directories and artifacts that will be used during the similarity calculation process.");
		this.codeUnit = codeUnit;
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

		Label codeUnitALabel = new Label(inputGroup, SWT.NONE);
		codeUnitALabel.setText("Main code unit:");
		Text codeUnitAText = new Text(inputGroup, SWT.BORDER);
		codeUnitAText.setEnabled(false);
		codeUnitAText.setLayoutData(new GridData(140, 16));
		codeUnitAText.setText(codeUnit.getName());

		Label modelClassALabel = new Label(inputGroup, SWT.NONE);
		modelClassALabel.setText("Model class:");
		TypeSelector modelClassASelec = new TypeSelector(inputGroup);
		/*
		 * We listen to the changes in the text of the selector to know when the
		 * user has selected a model class and update our attribute.
		 */
		modelClassASelec.getTypeText().addModifyListener(new ModifyListener() {
			/** {@inheritDoc} */
			public void modifyText(ModifyEvent e) {
				modelClass = modelClassASelec.getType();
				getContainer().updateButtons();
			}
		});

		/** Other classes */
		Group othersGroup = new Group(inputGroup, SWT.SHADOW_NONE);
		othersGroup.setText("Other code units");
		othersGroup.setLayout(new FillLayout());
		GridData othersLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		othersLayoutData.horizontalSpan = 2;
		othersGroup.setLayoutData(othersLayoutData);

		table = new CodeUnitsTable(othersGroup);

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

	public IType getModelClass() {
		return modelClass;
	}

	/**
	 * Returns the code units that will be compared to the main code unit. For
	 * each code unit, a related model class can be specified, or null
	 * otherwise.
	 * 
	 * @return A map with the code units and their optional model classes.
	 */
	public Map<CodeUnit, IType> getOtherCodeUnits() {
		return table.getCodeUnits();
	}
}
