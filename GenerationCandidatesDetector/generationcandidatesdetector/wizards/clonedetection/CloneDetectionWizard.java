package generationcandidatesdetector.wizards.clonedetection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import clonedetection.CloneDetectionProcess;
import clonedetection.CloneDetectionProcessConfiguration;
import clonedetection.CloneDetectionResult;
import clonedetection.simian.SimianCloneDetector;
import freemarker.template.TemplateException;
import generationcandidatesdetector.helpers.DialogHelpers;
import generationcandidatesdetector.output.OutputWriter;
import utils.Logger;
import utils.Postprocessor;

/**
 * Wizard for executing the clone detection feature.
 */
public class CloneDetectionWizard extends Wizard {
	/** Pages conforming the wizard. */
	private CloneDetectionIOPage ioPage;
	private CloneDetectionToolPage toolPage;
	private CloneDetectionSimianPage simianPage;

	/** Configuration of the clone detection process */
	private CloneDetectionProcessConfiguration configuration;

	/** List of initial files selected. */
	private List<IFile> files;

	public CloneDetectionWizard(List<IFile> files) {
		super();
		setNeedsProgressMonitor(true);
		setHelpAvailable(false);

		this.files = files;
		this.configuration = CloneDetectionProcessConfiguration.getDefaultConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getWindowTitle() {
		return "Clone detection";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		ioPage = new CloneDetectionIOPage(files);
		toolPage = new CloneDetectionToolPage(configuration);
		simianPage = new CloneDetectionSimianPage(configuration);
		addPage(ioPage);
		addPage(toolPage);
		addPage(simianPage);
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
		if (page instanceof CloneDetectionIOPage) {
			return toolPage;
		}
		if (page instanceof CloneDetectionToolPage) {
			// Depending on the selected tool we will show one wizard page
			// or the other.
			if (configuration.getCloneDetector() instanceof SimianCloneDetector) {
				return simianPage;
			}
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
		// Run the clone detection process:

		CloneDetectionProcess process = new CloneDetectionProcess(configuration);
		// Get the list of full names of the files
		List<String> fileFullNames = new LinkedList<String>();
		for (IFile file : ioPage.getFiles().values()) {
			// We include some basic, hard-coded filter here
			// TODO Integrate the filtering possibilities with the configuration
			// so that he can customize what gets filtered and what does not.
			if (file.getFileExtension() != null && !file.getFileExtension().equals("jar")) {
				fileFullNames.add(file.getRawLocation().toOSString());
			}
		}
		Logger.log("Initializing clone detection...");
		
		CloneDetectionResult result = process.detectClones(fileFullNames);
		
		Logger.log("Finished clone detection");
		
		Logger.log("Postprocessing the results...");
		
		Postprocessor.postProcess(result);
		
		Logger.log("Generating the output...");

		// Generate the output
		try {
			OutputWriter.getInstance().generateOutput(result, ioPage.getOutputPath(), null);
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
