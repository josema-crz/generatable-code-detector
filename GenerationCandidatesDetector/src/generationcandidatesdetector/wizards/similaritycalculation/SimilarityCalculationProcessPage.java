package generationcandidatesdetector.wizards.similaritycalculation;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import similaritycalculation.GlobalSimilarityCalculationProcessConfiguration;
import similaritycalculation.gst.GSTSimilarityCalculationAlgorithm;

/**
 * Page for the configuration of the global similarity calculation process.
 */
public class SimilarityCalculationProcessPage extends WizardPage {
	private GlobalSimilarityCalculationProcessConfiguration config;
	private Table stepWeightsTable;
	private Combo algorithmCombo;

	public SimilarityCalculationProcessPage(GlobalSimilarityCalculationProcessConfiguration config) {
		super("Similarity calculation process configuration");
		setTitle("Similarity calculation process configuration");
		setDescription("Set the configuration values for the similarity calculation process.");
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		/** ---- Algorithm selection ---- */
		Composite algComp = new Composite(container, SWT.NONE);
		GridLayout algCompLayout = new GridLayout();
		algCompLayout.numColumns = 2;
		algComp.setLayout(algCompLayout);
		Label algLabel = new Label(algComp, SWT.NONE);
		algLabel.setText("Algorithm:");
		algorithmCombo = new Combo(algComp, SWT.READ_ONLY);
		algorithmCombo.setItems(new String[] { "Greedy String Tiling (GST)" });
		algorithmCombo.select(0);
		algorithmCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		// Update the configuration when an algorithm gets selected.
		algorithmCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateConfig();
			}
		});

		/** ---- Pre-processing steps ---- */
		Group stepsGroup = new Group(container, SWT.SHADOW_NONE);
		stepsGroup.setText("Pre-processing steps");
		GridLayout stepsLayout = new GridLayout();
		stepsLayout.numColumns = 2;
		stepsGroup.setLayout(stepsLayout);
		stepsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		/** Table creation */
		int[] steps = new int[] { 0, 1, 2, 3, 4, 10 };
		stepWeightsTable = new Table(stepsGroup, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
		stepWeightsTable.setLinesVisible(true);
		stepWeightsTable.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		stepWeightsTable.setLayoutData(data);
		String[] titles = { "Step nº", "Step weight" };
		for (String title : titles) {
			TableColumn column = new TableColumn(stepWeightsTable, SWT.NONE);
			column.setText(title);
		}
		for (int step : steps) {
			TableItem item = new TableItem(stepWeightsTable, SWT.NONE);
			item.setText(0, Integer.toString(step));
		}
		for (int i = 0; i < titles.length; i++) {
			stepWeightsTable.getColumn(i).pack();
		}

		// Editing the second column, with the steps weights.
		final int EDITABLECOLUMN = 1;

		/** Check listener */
		stepWeightsTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TableItem item = (TableItem) event.item;
					// Empty the weight cell when the row gets unchecked.
					if (!item.getChecked()) {
						item.setText(EDITABLECOLUMN, "");
					}
					updateConfig();
				}
			}
		});

		/** Steps weights edition */
		final TableEditor editor = new TableEditor(stepWeightsTable);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		stepWeightsTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				// Only the checked rows are editable
				if (item == null || !item.getChecked())
					return;

				// The control that will be the editor must be a child of the
				// Table. We use a spinner.
				final Spinner spinner = new Spinner(stepWeightsTable, SWT.NONE);
				// allow 2 decimal places
				spinner.setDigits(2);
				// set the minimum value to 0.01
				spinner.setMinimum(0);
				// set the maximum value to 1
				spinner.setMaximum(100);
				// set the increment value to 0.10
				spinner.setIncrement(10);
				spinner.addModifyListener(me -> {
					Spinner s = (Spinner) editor.getEditor();
					editor.getItem().setText(EDITABLECOLUMN, s.getText());
					// We update the configuration with the new value.
					updateConfig();
				});
				spinner.setFocus();
				editor.setEditor(spinner, item, EDITABLECOLUMN);
			}
		});

		setControl(container);
		initControl(); // Initialize the configuration
	}

	/**
	 * Initializes the control values with the configuration values.
	 */
	private void initControl() {
		// Algorithm selection
		if (config.getAlgorithm() instanceof GSTSimilarityCalculationAlgorithm) {
			algorithmCombo.select(0);
		}

		// Pre-processing steps
		int stepsIndex = 0;
		// We assume the steps defined in the configuration are sorted
		for (TableItem item : stepWeightsTable.getItems()) {
			// If the step in the table is in the config, we check it and set
			// the weight
			if (stepsIndex < config.getSteps().size()
					&& item.getText(0).equals(config.getSteps().get(stepsIndex).toString())) {
				item.setChecked(true);
				item.setText(1, config.getStepWeights().get(stepsIndex).toString());
				stepsIndex++;
			}
			// Otherwise we continue looking.
			// The steps must be sorted both in the config and in the table for
			// this to work.
		}
	}

	/**
	 * Updates the configuration with the values set by the user in the wizard
	 * page.
	 */
	private void updateConfig() {
		// We set the algorithm
		if (algorithmCombo.getSelectionIndex() == 0) {
			config.setAlgorithm(new GSTSimilarityCalculationAlgorithm());
		}

		// We set the steps and their weights
		config.getSteps().clear();
		config.getStepWeights().clear();
		for (TableItem item : stepWeightsTable.getItems()) {
			if (item.getChecked()) {
				config.getSteps().add(Integer.parseInt(item.getText(0)));
				// If the step was checked but no weight was provided, we assign
				// 0 as the weight.
				double stepWeight = 0.0;
				if (!item.getText(1).isEmpty()) {
					try {
						stepWeight = Double.parseDouble(item.getText(1));
					} catch (NumberFormatException e) {
						// Do nothing, keep the default 0.0 value.
					}
				}
				config.getStepWeights().add(stepWeight);
			}
		}
		
		// TODO Include weights validation, so that they add up to 1.
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && getErrorMessage() == null;
	}
}
