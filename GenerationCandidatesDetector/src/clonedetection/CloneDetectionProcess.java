package clonedetection;

import java.util.List;

/**
 * Represents a clone detection process, which detects clones with a certain
 * clone detector.
 */
public class CloneDetectionProcess {
	/** Configuration values for the process */
	private CloneDetectionProcessConfiguration configuration;

	/**
	 * Initializes a default process.
	 */
	public CloneDetectionProcess() {
		configuration = CloneDetectionProcessConfiguration.getDefaultConfiguration();
	}

	/**
	 * Initializes a process with a given configuration.
	 * 
	 * @param configuration
	 *            The configuration values for the process.
	 */
	public CloneDetectionProcess(CloneDetectionProcessConfiguration configuration) {
		this.configuration = configuration;
	}

	public CloneDetectionProcessConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(CloneDetectionProcessConfiguration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * Returns the result of the clone detection process on a list of files.
	 * 
	 * @param files
	 *            The paths of the files where the clones are searched
	 * @return A result containing all the groups of clones found in the files
	 */
	public CloneDetectionResult detectClones(List<String> files) {
		return configuration.getCloneDetector().detectClones(files);
	}
}
