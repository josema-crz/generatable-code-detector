package generationcandidatesdetector.wizards.similaritycalculation;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public abstract class SimilarityCalculationIOPage extends WizardPage {
	/** Path where the result will be created. */
	protected String outputPath;

	protected SimilarityCalculationIOPage(String pageName) {
		super(pageName);
	}

	@Override
	public abstract void createControl(Composite parent);

	public String getOutputPath() {
		return outputPath;
	}
}
