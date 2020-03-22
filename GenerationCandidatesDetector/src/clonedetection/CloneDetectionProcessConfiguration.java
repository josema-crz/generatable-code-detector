package clonedetection;

import clonedetection.simian.SimianCloneDetector;

/**
 * Configuration values for the clone detection process.
 */
public class CloneDetectionProcessConfiguration {
	/** Clone detector used by the process to detect the clones */
	private CloneDetector cloneDetector;
	
	/**
	 * Returns a configuration with the default values.
	 * @return The default configuration
	 */
	public static CloneDetectionProcessConfiguration getDefaultConfiguration() {
		CloneDetectionProcessConfiguration config = new CloneDetectionProcessConfiguration();
		// Set Simian as the default clone detector
		config.setCloneDetector(new SimianCloneDetector());
		return config;
	}

	public CloneDetector getCloneDetector() {
		return cloneDetector;
	}

	public void setCloneDetector(CloneDetector cloneDetector) {
		this.cloneDetector = cloneDetector;
	}	
}
