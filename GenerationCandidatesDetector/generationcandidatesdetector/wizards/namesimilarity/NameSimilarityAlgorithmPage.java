package generationcandidatesdetector.wizards.namesimilarity;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import namesimilarity.NameSimilarityAlgorithmConfiguration;
import namesimilarity.NameSimilarityHeuristic;

/**
 * Wizard page for the configuration of the name similarity algorithm.
 */
public class NameSimilarityAlgorithmPage extends WizardPage {
	private NameSimilarityAlgorithmConfiguration config;

	/**
	 * String used in the name replacements.
	 */
	private Text replacementText;

	/**
	 * Available heuristics.
	 */
	private Map<String, NameSimilarityHeuristic> heuristics;

	/**
	 * Button to set whether the similarity calculation process will be applied
	 * on the results or not.
	 */
	private Button isSimilarityCalculated;

	public NameSimilarityAlgorithmPage(NameSimilarityAlgorithmConfiguration config) {
		super("Name similarity algorithm configuration");
		setTitle("Name similarity algorithm configuration");
		setDescription("Set the configuration values for the similarity calculation process.");
		this.config = config;

		// Initialize available heuristics.
		heuristics = new HashMap<String, NameSimilarityHeuristic>();
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		Composite repComp = new Composite(container, SWT.NONE);
		GridLayout repCompLayout = new GridLayout();
		repCompLayout.numColumns = 2;
		repComp.setLayout(repCompLayout);
		Label repLabel = new Label(repComp, SWT.NONE);
		repLabel.setText("*Replacement identifier:");
		replacementText = new Text(repComp, SWT.BORDER);
		replacementText.setLayoutData(new GridData(140, 16));
		replacementText.addModifyListener(me -> {
			config.setReplacementIdentifier(replacementText.getText());
		});
		
		isSimilarityCalculated = new Button(container, SWT.CHECK);
		isSimilarityCalculated.setText("Calculate the similarity between the identified structures of files");
		isSimilarityCalculated.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				getWizard().getContainer().updateButtons();
			}
		});

		// ---- Heuristics ----

		Group heuristicsGroup = new Group(container, SWT.SHADOW_NONE);
		heuristicsGroup.setText("Heuristics");
		GridLayout heuristicsLayout = new GridLayout();
		heuristicsLayout.numColumns = 2;
		heuristicsGroup.setLayout(heuristicsLayout);
		heuristicsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		if (heuristics.isEmpty()) {
			Label noHeuristicsLabel = new Label(heuristicsGroup, SWT.NONE);
			noHeuristicsLabel.setText("No heuristics available");
		} else {
			// TODO Show available heuristics with checkboxes
		}

		setControl(container);
		initControl(); // Initialize the configuration
	}

	/**
	 * Initializes the control values with the configuration values.
	 */
	private void initControl() {
		replacementText.setText(config.getReplacementIdentifier());

		// TODO Initialize selected heuristics
	}

	@Override
	public boolean canFlipToNextPage() {
		return replacementText.getText() != null & !replacementText.getText().isEmpty() && super.canFlipToNextPage()
				&& getErrorMessage() == null;
	}
	
	public boolean isSimilarityCalculated() {
		return isSimilarityCalculated.getSelection();
	}
}
