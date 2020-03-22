package generationcandidatesdetector.wizards.clonedetection;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import clonedetection.CloneDetectionProcessConfiguration;
import clonedetection.simian.SimianCloneDetector;
import clonedetection.simian.SimianCloneDetectorConfiguration;

/**
 * Page for the configuration of the GST similarity calculation algorithm.
 */
public class CloneDetectionSimianPage extends WizardPage {
	private CloneDetectionProcessConfiguration configuration;

	// Widgets for the configuration values
	private Spinner thresholdSpinner;
	private Combo langCombo;
	private Button ignoreCurlyBraces, ignoreIdentifiers, ignoreStrings, ignoreNumbers, ignoreCharacters, ignoreLiterals, ignoreSubtypeNames, ignoreModifiers, ignoreVariableNames;

	public CloneDetectionSimianPage(CloneDetectionProcessConfiguration configuration) {
		super("Simian configuration");
		setTitle("Simian configuration");
		setDescription("Set the configuration values for the Simian clone detection tool.");

		this.configuration = configuration;
	}

	@Override
	public void createControl(Composite parent) {
		SimianCloneDetector cloneDetector = (SimianCloneDetector) configuration.getCloneDetector();

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		// TODO Add some help information to understand each field.

		/** ---- Threshold selection ---- */
		Composite thresholdComp = new Composite(container, SWT.NONE);
		GridLayout thresholdCompLayout = new GridLayout();
		thresholdCompLayout.numColumns = 2;
		thresholdComp.setLayout(thresholdCompLayout);
		Label thresholdLabel = new Label(thresholdComp, SWT.NONE);
		thresholdLabel.setText("Threshold:");
		thresholdSpinner = new Spinner(thresholdComp, SWT.NONE);
		// allow 0 decimal places
		thresholdSpinner.setDigits(0);
		// set the minimum value to 2 (Simian's minimum)
		thresholdSpinner.setMinimum(2);
		// set the increment value to 1
		thresholdSpinner.setIncrement(1);
		thresholdSpinner.addModifyListener(me -> {
			cloneDetector.getConfiguration().setThreshold(thresholdSpinner.getSelection());
		});

		/** ---- Language selection ---- */
		Composite langComp = new Composite(container, SWT.NONE);
		GridLayout langCompLayout = new GridLayout();
		langCompLayout.numColumns = 2;
		langComp.setLayout(langCompLayout);
		Label langLabel = new Label(langComp, SWT.NONE);
		langLabel.setText("Default language:");
		langCombo = new Combo(langComp, SWT.READ_ONLY);
		langCombo.setItems(cloneDetector.getConfiguration().getAvailableLanguages());
		langCombo.select(0);
		langCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		// Update the configuration when a result constructor gets selected.
		langCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cloneDetector.getConfiguration().setDefaultLanguage(langCombo.getItem(langCombo.getSelectionIndex()));
			}
		});
		
		/** ---- Ignore configuration values ---- */
		Group ignoreGroup = new Group(container, SWT.SHADOW_NONE);
		ignoreGroup.setText("Ignore the following...");
		GridLayout ignoreLayout = new GridLayout();
		ignoreLayout.numColumns = 3;
		ignoreGroup.setLayout(ignoreLayout);
		ignoreGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		ignoreCurlyBraces = new Button(ignoreGroup, SWT.CHECK);
		ignoreCurlyBraces.setText("Curly braces");
		ignoreCurlyBraces.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreCurlyBraces(ignoreCurlyBraces.getSelection());
			}
		});
		
		ignoreIdentifiers = new Button(ignoreGroup, SWT.CHECK);
		ignoreIdentifiers.setText("Identifiers");
		ignoreIdentifiers.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreIdentifiers(ignoreIdentifiers.getSelection());
			}
		});
		
		ignoreStrings = new Button(ignoreGroup, SWT.CHECK);
		ignoreStrings.setText("Strings");
		ignoreStrings.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreStrings(ignoreStrings.getSelection());
			}
		});
		
		ignoreNumbers = new Button(ignoreGroup, SWT.CHECK);
		ignoreNumbers.setText("Numbers");
		ignoreNumbers.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreNumbers(ignoreNumbers.getSelection());
			}
		});
		
		ignoreCharacters = new Button(ignoreGroup, SWT.CHECK);
		ignoreCharacters.setText("Characters");
		ignoreCharacters.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreCharacters(ignoreCharacters.getSelection());
			}
		});
		
		ignoreLiterals = new Button(ignoreGroup, SWT.CHECK);
		ignoreLiterals.setText("Literals");
		ignoreLiterals.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreLiterals(ignoreLiterals.getSelection());
			}
		});
		
		ignoreSubtypeNames = new Button(ignoreGroup, SWT.CHECK);
		ignoreSubtypeNames.setText("Subtype names");
		ignoreSubtypeNames.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreSubtypeNames(ignoreSubtypeNames.getSelection());
			}
		});
		
		ignoreModifiers = new Button(ignoreGroup, SWT.CHECK);
		ignoreModifiers.setText("Modifiers");
		ignoreModifiers.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreModifiers(ignoreModifiers.getSelection());
			}
		});
		
		ignoreVariableNames = new Button(ignoreGroup, SWT.CHECK);
		ignoreVariableNames.setText("Variable names");
		ignoreVariableNames.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				cloneDetector.getConfiguration().setIgnoreVariableNames(ignoreVariableNames.getSelection());
			}
		});

		setControl(container);
		initControl(); // Initialize the configuration
	}

	/**
	 * Initializes the control values with the configuration values.
	 */
	private void initControl() {
		SimianCloneDetectorConfiguration simianConfig = ((SimianCloneDetector) configuration.getCloneDetector()).getConfiguration();
		
		thresholdSpinner.setSelection(simianConfig.getThreshold());
		langCombo.setText(simianConfig.getDefaultLanguage());
		ignoreCurlyBraces.setSelection(simianConfig.isIgnoreCurlyBraces());
		ignoreIdentifiers.setSelection(simianConfig.isIgnoreIdentifiers());
		ignoreStrings.setSelection(simianConfig.isIgnoreStrings());
		ignoreNumbers.setSelection(simianConfig.isIgnoreNumbers());
		ignoreCharacters.setSelection(simianConfig.isIgnoreCharacters());
		ignoreLiterals.setSelection(simianConfig.isIgnoreLiterals());
		ignoreSubtypeNames.setSelection(simianConfig.isIgnoreSubtypeNames());
		ignoreModifiers.setSelection(simianConfig.isIgnoreModifiers());
		ignoreVariableNames.setSelection(simianConfig.isIgnoreVariableNames());
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && getErrorMessage() == null;
	}

}
