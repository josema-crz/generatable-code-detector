package generationcandidatesdetector.wizards.similaritycalculation;

import similaritycalculation.CodeUnit;
import similaritycalculation.IGlobalSimilarityCalculationProcess;
import similaritycalculation.MultipleGlobalSimilarityCalculationResult;
import similaritycalculation.SimilarityCalculationException;

/**
 * Wizard for calculating the similarity between the selected artifact and a
 * set of artifacts provided by the user.
 */
public class SimilarityCalculation1XWizard extends SimilarityCalculationWizard {
	/**
	 * Code unit being compared, created from the artifact selected by the user.
	 */
	private CodeUnit codeUnit;

	public SimilarityCalculation1XWizard(CodeUnit codeUnit) {
		super();
		this.codeUnit = codeUnit;
	}

	@Override
	protected SimilarityCalculationIOPage createIOPage() {
		return new SimilarityCalculation1XIOPage(codeUnit);
	}

	@Override
	protected MultipleGlobalSimilarityCalculationResult launchSimilarityProcess(
			IGlobalSimilarityCalculationProcess process) throws SimilarityCalculationException {
		return process.calculateSimilarity(codeUnit, ((SimilarityCalculation1XIOPage) ioPage).getModelClass(),
				((SimilarityCalculation1XIOPage) ioPage).getOtherCodeUnits());
	}
}
