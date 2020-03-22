package generationcandidatesdetector.wizards.similaritycalculation;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import similaritycalculation.GlobalSimilarityCalculationProcessConfiguration;
import similaritycalculation.gst.GSTSimilarityCalculationAlgorithm;
import similaritycalculation.gst.TokenGSTResultConstructor;
import similaritycalculation.gst.asttokenizer.EclipseASTTokenizer;

/**
 * Page for the configuration of the GST similarity calculation algorithm.
 */
public class SimilarityCalculationGSTAlgorithmPage extends WizardPage {
	private GlobalSimilarityCalculationProcessConfiguration config;
	private Combo resultCombo, tokenizerCombo;
	private Spinner matchSpinner;

	public SimilarityCalculationGSTAlgorithmPage(GlobalSimilarityCalculationProcessConfiguration config) {
		super("Similarity calculation algorithm configuration");
		setTitle("Similarity calculation algorithm configuration");
		setDescription("Set the configuration values for the similarity calculation algorithm.");
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		GSTSimilarityCalculationAlgorithm algorithm = (GSTSimilarityCalculationAlgorithm) config.getAlgorithm();
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		// TODO Add some help information to understand each field.

		/** ---- Result constructor selection ---- */
		Composite resultComp = new Composite(container, SWT.NONE);
		GridLayout resultCompLayout = new GridLayout();
		resultCompLayout.numColumns = 2;
		resultComp.setLayout(resultCompLayout);
		Label resultLabel = new Label(resultComp, SWT.NONE);
		resultLabel.setText("Result constructor:");
		resultCombo = new Combo(resultComp, SWT.READ_ONLY);
		resultCombo.setItems(new String[] { "Token constructor" });
		resultCombo.select(0);
		resultCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		// Update the configuration when a result constructor gets selected.
		resultCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (resultCombo.getSelectionIndex() == 0) {
					algorithm.getConfiguration().setResultConstructor(new TokenGSTResultConstructor());
				}
			}
		});

		/** ---- Tokenizer selection ---- */
		Composite tokenizerComp = new Composite(container, SWT.NONE);
		GridLayout tokenizerCompLayout = new GridLayout();
		tokenizerCompLayout.numColumns = 2;
		tokenizerComp.setLayout(tokenizerCompLayout);
		Label tokenizerLabel = new Label(tokenizerComp, SWT.NONE);
		tokenizerLabel.setText("Tokenizer:");
		tokenizerCombo = new Combo(tokenizerComp, SWT.READ_ONLY);
		tokenizerCombo.setItems(new String[] { "Eclipse AST Tokenizer" });
		tokenizerCombo.select(0);
		tokenizerCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		// Update the configuration when a tokenizer gets selected.
		tokenizerCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tokenizerCombo.getSelectionIndex() == 0) {
					algorithm.getConfiguration().setTokenizer(new EclipseASTTokenizer());
				}
			}
		});

		/** ---- Minimum match length selection ---- */
		Composite matchComp = new Composite(container, SWT.NONE);
		GridLayout matchCompLayout = new GridLayout();
		matchCompLayout.numColumns = 2;
		matchComp.setLayout(matchCompLayout);
		Label matchLabel = new Label(matchComp, SWT.NONE);
		matchLabel.setText("Minimum match length:");
		matchSpinner = new Spinner(matchComp, SWT.NONE);
		// allow 0 decimal places
		matchSpinner.setDigits(0);
		// set the minimum value to 1
		matchSpinner.setMinimum(1);
		// set the maximum value to 100
		matchSpinner.setMaximum(100);
		// set the increment value to 1
		matchSpinner.setIncrement(1);
		matchSpinner.addModifyListener(me -> {
			algorithm.getConfiguration().setMinimumMatchLength(matchSpinner.getSelection());
		});

		setControl(container);
		initControl(); // Initialize the configuration
	}

	/**
	 * Initializes the control values with the configuration values.
	 */
	private void initControl() {
		GSTSimilarityCalculationAlgorithm algorithm = (GSTSimilarityCalculationAlgorithm) config.getAlgorithm();

		// Result constructor selection
		if (algorithm.getConfiguration().getResultConstructor() instanceof TokenGSTResultConstructor) {
			resultCombo.select(0);
		}
		// Tokenizer selection
		if (algorithm.getConfiguration().getTokenizer() instanceof EclipseASTTokenizer) {
			tokenizerCombo.select(0);
		}
		// Minimum match length selection
		matchSpinner.setSelection(algorithm.getConfiguration().getMinimumMatchLength());
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && getErrorMessage() == null;
	}

}
