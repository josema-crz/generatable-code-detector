package clonedetection.simian;

import java.io.IOException;
import java.util.List;

import com.harukizaemon.simian.Checker;
import com.harukizaemon.simian.FileLoader;
import com.harukizaemon.simian.StreamLoader;

import clonedetection.CloneDetectionResult;
import clonedetection.CloneDetector;

/**
 * Clone detector implementation that uses the Simian clone detection tool.
 */
public class SimianCloneDetector implements CloneDetector {
	/** Configuration values */
	private SimianCloneDetectorConfiguration configuration;

	/**
	 * Creates a SimianCloneDetector with the specified configuration.
	 * 
	 * @param configuration
	 *            Configuration values.
	 */
	public SimianCloneDetector(SimianCloneDetectorConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Creates a SimianCloneDetector with the default configuration.
	 */
	public SimianCloneDetector() {
		this(SimianCloneDetectorConfiguration.getDefaultConfiguration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CloneDetectionResult detectClones(List<String> files) {
		// Use the Simian API to launch the tool and obtain the clones:

		SimianResultConstructor resultConstructor = new SimianResultConstructor(files.size());
		Checker checker = new Checker(resultConstructor, configuration.getOptions());
		StreamLoader streamLoader = new StreamLoader(checker);
		FileLoader fileLoader = new FileLoader(streamLoader);
		for (String file : files) {
			try {
				fileLoader.load(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Run the process. The listener methods in the result constructor will
		// be invoked for every clone found and will build the result.
		checker.check();

		return resultConstructor.getResult();
	}

	public SimianCloneDetectorConfiguration getConfiguration() {
		return configuration;
	}

}
