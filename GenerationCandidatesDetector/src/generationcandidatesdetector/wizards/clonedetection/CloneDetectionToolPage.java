package generationcandidatesdetector.wizards.clonedetection;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import clonedetection.CloneDetectionProcessConfiguration;
import clonedetection.simian.SimianCloneDetector;

/**
 * Wizard page for the configuration of the name similarity algorithm.
 */
public class CloneDetectionToolPage extends WizardPage {
	/** CloneDetector selection combo */
	private Combo toolCombo;
	/** Process configuration */
	private CloneDetectionProcessConfiguration configuration;

	public CloneDetectionToolPage(CloneDetectionProcessConfiguration configuration) {
		super("Clone detection tool selection");
		setTitle("Clone detection tool selection");
		setDescription("Select the tool that will be used to detect the clones.");

		this.configuration = configuration;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		/** ---- Algorithm selection ---- */
		Composite toolComp = new Composite(container, SWT.NONE);
		GridLayout toolCompLayout = new GridLayout();
		toolCompLayout.numColumns = 2;
		toolComp.setLayout(toolCompLayout);
		Label toolLabel = new Label(toolComp, SWT.NONE);
		toolLabel.setText("Tool:");
		toolCombo = new Combo(toolComp, SWT.READ_ONLY);
		toolCombo.setItems(new String[] { "Simian" });
		toolCombo.select(0);
		toolCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		// Update the configuration when a tool gets selected.
		toolCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (toolCombo.getSelectionIndex()) {
				case 0:
					configuration.setCloneDetector(new SimianCloneDetector());
					break;

				default:
					break;
				}
			}
		});

		setControl(container);
		initControl(); // Initialize the configuration
	}

	/**
	 * Initializes the control values according to the configuration.
	 */
	private void initControl() {
		if (configuration.getCloneDetector() instanceof SimianCloneDetector) {
			toolCombo.select(0);
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && getErrorMessage() == null;
	}
}
