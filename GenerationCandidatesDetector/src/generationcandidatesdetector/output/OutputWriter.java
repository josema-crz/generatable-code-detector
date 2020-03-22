package generationcandidatesdetector.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import clonedetection.CloneDetectionResult;
import clonedetection.CloneSet;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import namesimilarity.NameSimilarityResultGroup;
import similaritycalculation.GlobalSimilarityCalculationResult;
import similaritycalculation.MultipleGlobalSimilarityCalculationResult;

/**
 * Class with the methods to generate the output with the results of a certain
 * process. It returns the main file for the results, although additional files
 * may have been generated and be accessible from the returned file.
 */
public class OutputWriter {
	/** Singleton instance. */
	private static OutputWriter instance;
	/** Template configuration. */
	private Configuration cfg;

	/** Paths and names of the global files. */
	private static final String cssFolderName = "css";
	private static final String imgFolderName = "img";
	private static final String scriptsFolderName = "scripts";
	private static final String cssFileName = "styles.css";
	private static final String helpImgFileName = "help.png";
	private static final String arrowImgFileName = "arrow.jpg";
	private static final String functionsScriptFileName = "functions.js";
	private static final String outputFilesPath = "outputFiles";

	/** Template directory. */
	private static final String templateDirectory = "src/generationcandidatesdetector/output/templates";

	/** Counter used to name files. */
	private static int fileIdCounter = 0;

	private OutputWriter() throws IOException {
		/* Create and adjust the configuration */
		cfg = new Configuration(Configuration.VERSION_2_3_24);
		Bundle bundle = Platform.getBundle("GenerationCandidatesDetector");
		URL url = FileLocator.resolve(bundle.getEntry(templateDirectory));
		cfg.setDirectoryForTemplateLoading(new File(url.getPath()));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setAPIBuiltinEnabled(true);
		cfg.setLocale(Locale.US);
	}

	public static OutputWriter getInstance() throws IOException {
		if (instance == null) {
			instance = new OutputWriter();
		}
		return instance;
	}

	/**
	 * Uses the template engine to generate an output file with a specific
	 * template and root object.
	 * 
	 * @param root
	 *            Java object with the information needed to generate the output
	 *            file.
	 * @param templateName
	 *            Name of the template to be used.
	 * @param outputFile
	 *            Output file that will be written.
	 * @throws TemplateException
	 * @throws IOException
	 */
	private void writeOutput(Object root, String templateName, File outputFile) throws TemplateException, IOException {
		/* Get the template (uses cache internally) */
		Template temp = cfg.getTemplate(templateName);

		/* Merge data-model with template */
		Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));
		temp.process(root, out);
	}

	/**
	 * Generate the corresponding output files for a multiple global similarity
	 * result.
	 * 
	 * @param result
	 *            Similarity results.
	 * @param outputPath
	 *            Path where the output files will be located.
	 * @param parentFile
	 *            Optional. Parent file that will be referenced back from the
	 *            created files.
	 * @return The main output file created, which may have links to additional
	 *         files.
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws TemplateException
	 */
	public File generateOutput(MultipleGlobalSimilarityCalculationResult result, String outputPath, File parentFile)
			throws IOException, URISyntaxException, TemplateException {
		generateCommonFiles(outputPath);		

		String indexFileName;
		if (parentFile == null) {
			indexFileName = "index.html";
		} else {
			indexFileName = "multiple-similarity-result-" + getNextFileIdCounter() + ".html";
		}
		File indexFile = new File(outputPath + Path.SEPARATOR + indexFileName);

		// Create and store the files for every individual result.
		Map<File, GlobalSimilarityCalculationResult> outputResults = new HashMap<File, GlobalSimilarityCalculationResult>();
		for (GlobalSimilarityCalculationResult simResult : result.getResults()) {
			// Give a significant name.
			String fileName = simResult.getCodeUnitA().getName() + "_" + simResult.getCodeUnitB().getName()
					+ "-similarity.html";
			File outputFile = new File(outputPath + Path.SEPARATOR + fileName);

			// Create the root object with the information that the template
			// needs.
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("result", simResult);
			root.put("modelClassA", simResult.getModelClassA());
			root.put("modelClassB", simResult.getModelClassB());
			root.put("parentFile", indexFile);

			// Use the template to write the result in the output file.
			writeOutput(root, "similarity-individual-result.ftlh", outputFile);

			outputResults.put(outputFile, simResult);
		}

		// Generate the index file linking to all the individual results.
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("results", outputResults);
		root.put("resultFiles", new LinkedList<File>(outputResults.keySet()));
		root.put("parentFile", parentFile);
		// Use the template to write the result in the output file.
		writeOutput(root, "similarity-global-result.ftlh", indexFile);

		return indexFile;
	}

	/**
	 * Generate the corresponding output for the name similarity results.
	 * 
	 * @param completeResults
	 *            Name similarity results and, optionally, their corresponding
	 *            similarity measures.
	 * @param outputPath
	 *            Path where the output files will be located.
	 * @param parentFile
	 *            Optional. Parent file that will be referenced back from the
	 *            created files.
	 * @return The main output file created, which may have links to additional
	 *         files.
	 * @throws TemplateException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public File generateOutput(
			Map<NameSimilarityResultGroup, MultipleGlobalSimilarityCalculationResult> completeResults,
			String outputPath, File parentFile) throws IOException, URISyntaxException, TemplateException {
		generateCommonFiles(outputPath);

		// Index file with all the identified structures listed.
		String indexFileName;
		if (parentFile == null) {
			indexFileName = "index.html";
		} else {
			indexFileName = "namesimilarity-global-result-" + getNextFileIdCounter() + ".html";
		}
		File indexFile = new File(outputPath + Path.SEPARATOR + indexFileName);

		Map<File, NameSimilarityResultGroup> nameResultFiles = new HashMap<File, NameSimilarityResultGroup>();

		for (Entry<NameSimilarityResultGroup, MultipleGlobalSimilarityCalculationResult> entry : completeResults
				.entrySet()) {
			// Group index file with the information about the identified group
			// and an optional link to the similarity results.
			String groupFileName = entry.getKey().getName() + ".html";
			File groupFile = new File(outputPath + Path.SEPARATOR + groupFileName);

			Map<String, Object> root = new HashMap<String, Object>();
			root.put("result", entry.getKey());
			root.put("parentFile", indexFile);

			// Similarity results.
			if (entry.getValue() != null) {
				File groupSimilarityFile = generateOutput(entry.getValue(), outputPath, groupFile);
				root.put("similarityResult", entry.getValue());
				root.put("similarityResultFile", groupSimilarityFile);
			}

			// Use the template to write the result in the output file.
			writeOutput(root, "name-group-result.ftlh", groupFile);

			nameResultFiles.put(groupFile, entry.getKey());
		}

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("results", nameResultFiles);
		root.put("resultFiles", new LinkedList<File>(nameResultFiles.keySet()));
		root.put("simResults", completeResults);
		root.put("parentFile", parentFile);
		// Use the template to write the result in the output file.
		writeOutput(root, "name-global-result.ftlh", indexFile);

		return indexFile;
	}

	/**
	 * Generate the corresponding output for the clone detection result.
	 * 
	 * @param result
	 *            The result of the clone detection process.
	 * @param outputPath
	 *            Path where the output files will be located.
	 * @param parentFile
	 *            Optional. Parent file that will be referenced back from the
	 *            created files.
	 * @return The main output file created, which may have links to additional
	 *         files.
	 * @throws TemplateException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public File generateOutput(CloneDetectionResult result, String outputPath, File parentFile)
			throws IOException, URISyntaxException, TemplateException {
		generateCommonFiles(outputPath);

		// Index file with all the identified structures listed.
		String indexFileName;
		if (parentFile == null) {
			indexFileName = "index.html";
		} else {
			indexFileName = "clone-detection-result-" + getNextFileIdCounter() + ".html";
		}
		File indexFile = new File(outputPath + Path.SEPARATOR + indexFileName);

		// Create and store the files for every set.
		Map<File, CloneSet> setResults = new HashMap<File, CloneSet>();
		int index = 1;
		for (CloneSet cloneSet : result.getSets()) {
			// File with the information about each set
			String setFileName = "cloneset" + index + ".html";
			File setFile = new File(outputPath + Path.SEPARATOR + setFileName);

			Map<String, Object> root = new HashMap<String, Object>();
			root.put("set", cloneSet);
			root.put("parentFile", indexFile);			

			// Use the template to write the result in the output file.
			writeOutput(root, "clone-set-result.ftlh", setFile);

			setResults.put(setFile, cloneSet);

			index++;
		}

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("sets", setResults);
		root.put("setFiles", new LinkedList<File>(setResults.keySet()));
		root.put("parentFile", parentFile);
		// Use the template to write the result in the output file.
		writeOutput(root, "clone-global-result.ftlh", indexFile);

		return indexFile;
	}

	/**
	 * Generate the common files used by every HTML output file that can be
	 * generated using this class. This includes images, css files and scripts.
	 * 
	 * @param outputPath
	 *            Path where the output files are generated.
	 * @throws IOException
	 *             If there was an error creating the files.
	 * @throws URISyntaxException
	 *             If there was an error reading the bundle files.
	 */
	private static void generateCommonFiles(String outputPath) throws IOException, URISyntaxException {
		File outputFolder = new File(outputPath);
		if (outputFolder.exists() && outputFolder.isDirectory()) {
			Bundle bundle = Platform.getBundle("GenerationCandidatesDetector");

			// Copy the css file into a new folder
			File cssFolder = new File(outputPath + Path.SEPARATOR + cssFolderName);
			cssFolder.mkdir();
			URL fileURL = FileLocator.resolve(bundle.getEntry(outputFilesPath + Path.SEPARATOR + cssFileName));
			File cssFile = new File(fileURL.getPath());
			File newCss = new File(cssFolder.getAbsolutePath() + Path.SEPARATOR + cssFileName);
			if (!newCss.exists()) {
				Files.copy(cssFile.toPath(), newCss.toPath());
			}

			// Copy the help img file into a new folder
			File imgFolder = new File(outputPath + Path.SEPARATOR + imgFolderName);
			imgFolder.mkdir();
			fileURL = FileLocator.resolve(bundle.getEntry(outputFilesPath + Path.SEPARATOR + helpImgFileName));
			File helpImgFile = new File(fileURL.getPath());
			File newHelpImgFile = new File(imgFolder.getAbsolutePath() + Path.SEPARATOR + helpImgFileName);
			if (!newHelpImgFile.exists()) {
				Files.copy(helpImgFile.toPath(), newHelpImgFile.toPath());
			}

			// Copy the arrow img file into the img folder too
			fileURL = FileLocator.resolve(bundle.getEntry(outputFilesPath + Path.SEPARATOR + arrowImgFileName));
			File arrowImgFile = new File(fileURL.getPath());
			File newArrowImgFile = new File(imgFolder.getAbsolutePath() + Path.SEPARATOR + arrowImgFileName);
			if (!newArrowImgFile.exists()) {
				Files.copy(arrowImgFile.toPath(), newArrowImgFile.toPath());
			}

			// Copy the script file into a new folder
			File scriptFolder = new File(outputPath + Path.SEPARATOR + scriptsFolderName);
			scriptFolder.mkdir();
			fileURL = FileLocator.resolve(bundle.getEntry(outputFilesPath + Path.SEPARATOR + functionsScriptFileName));
			File functionsFile = new File(fileURL.getPath());
			File newFunctionsFile = new File(scriptFolder.getAbsolutePath() + Path.SEPARATOR + functionsScriptFileName);
			if (!newFunctionsFile.exists()) {
				Files.copy(functionsFile.toPath(), newFunctionsFile.toPath());
			}

		} else {
			throw new IllegalArgumentException("The output path is not a valid folder");
		}
	}

	private static int getNextFileIdCounter() {
		return fileIdCounter++;
	}
}
