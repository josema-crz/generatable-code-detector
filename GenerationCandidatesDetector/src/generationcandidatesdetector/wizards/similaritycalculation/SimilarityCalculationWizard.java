package generationcandidatesdetector.wizards.similaritycalculation;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import freemarker.template.TemplateException;
import generationcandidatesdetector.helpers.DialogHelpers;
import generationcandidatesdetector.output.OutputWriter;
import similaritycalculation.GlobalSimilarityCalculationProcess;
import similaritycalculation.GlobalSimilarityCalculationProcessConfiguration;
import similaritycalculation.IGlobalSimilarityCalculationProcess;
import similaritycalculation.MultipleGlobalSimilarityCalculationResult;
import similaritycalculation.SimilarityCalculationException;
import similaritycalculation.gst.GSTSimilarityCalculationAlgorithm;

/**
 * Wizard for calculating the similarity between two or more artifacts. It must
 * be specialized depending on the way the wizard asks for the input
 * information.
 */
public abstract class SimilarityCalculationWizard extends Wizard {
	/** Pages conforming the wizard. */
	protected SimilarityCalculationIOPage ioPage;
	private SimilarityCalculationProcessPage processPage;
	private SimilarityCalculationGSTAlgorithmPage gstAlgorithmPage;
	/**
	 * Configuration used in the similarity calculation process, built with the
	 * information provided by the user.
	 */
	private GlobalSimilarityCalculationProcessConfiguration config;

	public SimilarityCalculationWizard() {
		super();
		setNeedsProgressMonitor(true);
		setHelpAvailable(false);
		// If the user finishes the wizard without specifying values for the
		// configuration, the default one will be used.
		config = GlobalSimilarityCalculationProcessConfiguration.getDefaultConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getWindowTitle() {
		return "Similarity calculation";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		ioPage = createIOPage();
		processPage = new SimilarityCalculationProcessPage(config);
		gstAlgorithmPage = new SimilarityCalculationGSTAlgorithmPage(config);
		addPage(ioPage);
		addPage(processPage);
		addPage(gstAlgorithmPage);
	}

	/**
	 * Gets the WizardPage used for asking the user for the information
	 * regarding the input and output of the process.
	 * 
	 * @return The I/O WizardPage.
	 */
	protected abstract SimilarityCalculationIOPage createIOPage();

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
		if (page instanceof SimilarityCalculationIOPage) {
			return processPage;
		}
		if (page instanceof SimilarityCalculationProcessPage) {
			// Depending on the selected algorithm we will show one wizard page
			// or the other.
			if (config.getAlgorithm() instanceof GSTSimilarityCalculationAlgorithm) {
				return gstAlgorithmPage;
			}
		}
		if (page instanceof SimilarityCalculationGSTAlgorithmPage) {
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
		// We create a similarity calculation process with the specified
		// configuration.
		IGlobalSimilarityCalculationProcess process = new GlobalSimilarityCalculationProcess(config);

		try {
			// Launch the whole process to get the final results.
			MultipleGlobalSimilarityCalculationResult result = launchSimilarityProcess(process);

			// Generate the output files.
			OutputWriter.getInstance().generateOutput(result, ioPage.getOutputPath(), null);

		} catch (SimilarityCalculationException e) {
			DialogHelpers.showErrorDialog(getShell(), "Error",
					"There was an error during the similarity calculation process: '" + e.getMessage() + "'.");
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

	/**
	 * Launches the similarity calculation process and obtains the results.
	 * 
	 * @param process
	 *            The global similarity calculation process to launch.
	 * @return The global similarity results.
	 * @throws SimilarityCalculationException
	 *             When there is an error during the similarity calculation
	 *             process.
	 */
	protected abstract MultipleGlobalSimilarityCalculationResult launchSimilarityProcess(
			IGlobalSimilarityCalculationProcess process) throws SimilarityCalculationException;
}
