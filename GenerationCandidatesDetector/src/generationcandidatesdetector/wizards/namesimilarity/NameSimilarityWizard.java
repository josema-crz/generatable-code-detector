package generationcandidatesdetector.wizards.namesimilarity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import freemarker.template.TemplateException;
import generationcandidatesdetector.helpers.DialogHelpers;
import generationcandidatesdetector.helpers.SimilarityCalculationHelpers;
import generationcandidatesdetector.output.OutputWriter;
import generationcandidatesdetector.wizards.similaritycalculation.SimilarityCalculationGSTAlgorithmPage;
import generationcandidatesdetector.wizards.similaritycalculation.SimilarityCalculationProcessPage;
import namesimilarity.NameSimilarityAlgorithm;
import namesimilarity.NameSimilarityAlgorithmConfiguration;
import namesimilarity.NameSimilarityResult;
import namesimilarity.NameSimilarityResultGroup;
import namesimilarity.NameSimilarityResultName;
import similaritycalculation.CodeUnit;
import similaritycalculation.GlobalSimilarityCalculationProcess;
import similaritycalculation.GlobalSimilarityCalculationProcessConfiguration;
import similaritycalculation.IGlobalSimilarityCalculationProcess;
import similaritycalculation.MultipleGlobalSimilarityCalculationResult;
import similaritycalculation.SimilarityCalculationException;
import similaritycalculation.gst.GSTSimilarityCalculationAlgorithm;
import utils.Logger;
import utils.Postprocessor;

/**
 * Wizard for finding structures of related names from a set of file names.
 */
public class NameSimilarityWizard extends Wizard {
	/** Pages conforming the wizard. */
	private NameSimilarityIOPage ioPage;
	private NameSimilarityAlgorithmPage algorithmPage;
	private SimilarityCalculationProcessPage simProcessPage;
	private SimilarityCalculationGSTAlgorithmPage simGstAlgorithmPage;

	/**
	 * Configuration used in the name similarity process, built with the
	 * information provided by the user.
	 */
	private NameSimilarityAlgorithmConfiguration config;

	/**
	 * Configuration used in the similarity calculation process, built with the
	 * information provided by the user.
	 */
	private GlobalSimilarityCalculationProcessConfiguration simConfig;

	/** List of initial files selected. */
	private List<IFile> files;

	public NameSimilarityWizard(List<IFile> files) {
		super();
		setNeedsProgressMonitor(true);
		setHelpAvailable(false);
		// If the user finishes the wizard without specifying values for the
		// configuration, the default one will be used.
		config = NameSimilarityAlgorithmConfiguration.getDefaultConfiguration();
		simConfig = GlobalSimilarityCalculationProcessConfiguration.getDefaultConfiguration();
		this.files = files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getWindowTitle() {
		return "Name similarity";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		ioPage = new NameSimilarityIOPage(files);
		algorithmPage = new NameSimilarityAlgorithmPage(config);
		simProcessPage = new SimilarityCalculationProcessPage(simConfig);
		simGstAlgorithmPage = new SimilarityCalculationGSTAlgorithmPage(simConfig);
		addPage(ioPage);
		addPage(algorithmPage);
		addPage(simProcessPage);
		addPage(simGstAlgorithmPage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IWizardPage getStartingPage() {
		return ioPage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof NameSimilarityIOPage) {
			return algorithmPage;
		}
		if (page instanceof NameSimilarityAlgorithmPage && algorithmPage.isSimilarityCalculated()) {
			return simProcessPage;
		}
		if (page instanceof SimilarityCalculationProcessPage) {
			// Depending on the selected algorithm we will show one wizard page
			// or the other.
			if (simConfig.getAlgorithm() instanceof GSTSimilarityCalculationAlgorithm) {
				return simGstAlgorithmPage;
			}
		}
		if (page instanceof SimilarityCalculationGSTAlgorithmPage
				|| (page instanceof NameSimilarityAlgorithmPage && !algorithmPage.isSimilarityCalculated())) {
			return null;
		}
		return super.getNextPage(page);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canFinish() {
		// The information in the first page is the only one that is necessary.
		// All the other config values in the other pages are optional.
		return ioPage.isPageComplete();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		// Run the name similarity process with the names of the files.
		NameSimilarityAlgorithm algorithm = new NameSimilarityAlgorithm(config);
		NameSimilarityResult result = algorithm.calculateNameSimilarity(
				new ArrayList<String>(ioPage.getFiles().keySet()));

		// The complete results include the identified groups and, optionally,
		// the similarity calculation results of the code units in each group.
		Map<NameSimilarityResultGroup, MultipleGlobalSimilarityCalculationResult> completeResults = new HashMap<NameSimilarityResultGroup, MultipleGlobalSimilarityCalculationResult>();

		Logger.log("Finished name similarity");

		int totalComp = 0;
		// Calculate the total number of similarity comparisons
		for (NameSimilarityResultGroup group : result.getGroups()) {
			totalComp += group.getResultNames().size() * (group.getResultNames().size() - 1) / 2;
		}

		int processedComp = 0;
		for (NameSimilarityResultGroup group : result.getGroups()) {
			// We ignore structures with only one element.
			if (group.getResultNames().size() > 1) {
				MultipleGlobalSimilarityCalculationResult simResult = null;

				if ((algorithmPage.isSimilarityCalculated() && group.getResultNames().size() > 900
						&& algorithmPage.isSimilarityCalculated() && group.getResultNames().size() < 1000)
						|| (algorithmPage.isSimilarityCalculated() && group.getResultNames().size() > 810
						&& algorithmPage.isSimilarityCalculated() && group.getResultNames().size() < 825)
						|| (algorithmPage.isSimilarityCalculated() && group.getResultNames().size() > 90
						&& algorithmPage.isSimilarityCalculated() && group.getResultNames().size() < 95)) {
					Logger.log("Code similarity: processing group with "
							+ group.getResultNames().size() * (group.getResultNames().size() - 1) / 2 + " comparisons");

					Map<CodeUnit, IType> codeUnits = new HashMap<CodeUnit, IType>();
					for (NameSimilarityResultName resultName : group.getResultNames()) {
						try {
							CodeUnit codeUnit = SimilarityCalculationHelpers
									.getCodeUnitFromObject(ioPage.getFiles().get(resultName.getOriginalName()));

							// We take only the first "derived from" name, since
							// the
							// similarity process only supports one model class
							// related to each code unit.
							IType modelClass = SimilarityCalculationHelpers
									.getTypeFromFile(ioPage.getFiles().get(resultName.getDerivedFrom().get(0)));

							codeUnits.put(codeUnit, modelClass);

						} catch (UnsupportedOperationException | CoreException e) {
							// Do nothing, keep trying with the other elements
							// in the group.
						}
					}

					// Launch the similarity calculation process.
					IGlobalSimilarityCalculationProcess process = new GlobalSimilarityCalculationProcess(simConfig);
					try {
						simResult = process.calculateSimilarity(codeUnits);
					} catch (SimilarityCalculationException e) {
						// e.printStackTrace();
						// Do nothing, a null result will be stored in the
						// complete results.
					}
				}
				// Store the result. There will be no similarity result if that
				// option was not specified.
				completeResults.put(group, simResult);

				processedComp += group.getResultNames().size() * (group.getResultNames().size() - 1) / 2;
				Logger.log("Code similarity: processed group (" + processedComp + "/" + totalComp + " comparisons)");
			}
		}
		
		Logger.log("Finished code similarity");
		
		Logger.log("Postprocessing results...");
		
		Postprocessor.postProcess(completeResults, ioPage.getFiles());
		//Postprocessor.count(completeResults, ioPage.getFiles());
		
		Logger.log("Generating output...");

		// Generate the output
		try {
			OutputWriter.getInstance().generateOutput(completeResults, ioPage.getOutputPath(), null);
		} catch (IOException e) {
			e.printStackTrace();
			DialogHelpers.showErrorDialog(getShell(), "Error",
					"There was an error generating the output. The output path may not be valid.");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			DialogHelpers.showErrorDialog(getShell(), "Error",
					"There was an error reading the inner files of the plugin. The output could not be generated.");
		} catch (TemplateException e) {
			e.printStackTrace();
			DialogHelpers.showErrorDialog(getShell(), "Error",
					"There was an internal error with the templates used to generate the output.");
		}

		return true;
	}
}
