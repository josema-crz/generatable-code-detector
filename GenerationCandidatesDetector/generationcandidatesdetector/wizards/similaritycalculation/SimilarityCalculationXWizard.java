package generationcandidatesdetector.wizards.similaritycalculation;

import java.util.List;

import similaritycalculation.CodeUnit;
import similaritycalculation.IGlobalSimilarityCalculationProcess;
import similaritycalculation.MultipleGlobalSimilarityCalculationResult;
import similaritycalculation.SimilarityCalculationException;

/**
 * Wizard for calculating the similarity between the selected artifact and a
 * set of artifacts provided by the user.
 */
public class SimilarityCalculationXWizard extends SimilarityCalculationWizard {
	/** Initial list of code units to be compared.*/
	private List<CodeUnit> codeUnits;
	
	public SimilarityCalculationXWizard(List<CodeUnit> codeUnits) {
		super();
		this.codeUnits = codeUnits;
	}

	@Override
	protected SimilarityCalculationIOPage createIOPage() {
		return new SimilarityCalculationXIOPage(codeUnits);
	}

	@Override
	protected MultipleGlobalSimilarityCalculationResult launchSimilarityProcess(
			IGlobalSimilarityCalculationProcess process) throws SimilarityCalculationException {
		return process.calculateSimilarity(((SimilarityCalculationXIOPage) ioPage).getCodeUnits());
	}
}
