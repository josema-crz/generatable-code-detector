package clonedetection.simian;

import com.harukizaemon.simian.Language;
import com.harukizaemon.simian.Option;
import com.harukizaemon.simian.Options;

/**
 * Contains some of the configuration values of the Simian tool. This class
 * wraps up the Options configuration object in the Simian library and offers
 * methods to access some of its configuration values, i.e. the ones that we
 * consider interesting.
 */
public class SimianCloneDetectorConfiguration {
	/** Simian native options object */
	private Options options;

	/**
	 * Initializes a new, empty configuration.
	 */
	public SimianCloneDetectorConfiguration() {
		options = new Options();
		// Fixed options:
		options.setOption(Option.REPORT_DUPLICATE_TEXT, true);
	}

	/**
	 * Creates a Simian configuration with the default values.
	 * 
	 * @return The default configuration.
	 */
	public static SimianCloneDetectorConfiguration getDefaultConfiguration() {
		SimianCloneDetectorConfiguration config = new SimianCloneDetectorConfiguration();
		config.setThreshold(5);
		config.setDefaultLanguage("JAVA");
		config.setIgnoreCurlyBraces(true);
		config.setIgnoreIdentifiers(false);
		config.setIgnoreStrings(true);
		config.setIgnoreNumbers(true);
		config.setIgnoreCharacters(true);
		config.setIgnoreLiterals(true);
		config.setIgnoreSubtypeNames(false);
		config.setIgnoreModifiers(true);
		config.setIgnoreVariableNames(true);
		return config;
	}
	
	/**
	 * Return the available languages to choose from.
	 * @return An array with the languages
	 */
	public String[] getAvailableLanguages() {
		String[] langs = new String[Language.values().size()];
		int index = 0;
		for (Language lang : Language.values()) {
			langs[index] = lang.toString();
			index++;
		}
		return langs;
	}

	public Options getOptions() {
		return options;
	}

	public int getThreshold() {
		return options.getThreshold();
	}

	public void setThreshold(int threshold) {
		options.setThreshold(threshold);
	}

	public String getDefaultLanguage() {
		return options.getOption(Option.DEFAULT_LANGUAGE).toString();
	}

	public void setDefaultLanguage(String defaultLanguage) {
		options.setOption(Option.DEFAULT_LANGUAGE, Language.valueOf(defaultLanguage));
	}

	public boolean isIgnoreCurlyBraces() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_CURLY_BRACES).toString());
	}

	public void setIgnoreCurlyBraces(boolean ignoreCurlyBraces) {
		options.setOption(Option.IGNORE_CURLY_BRACES, ignoreCurlyBraces);
	}

	public boolean isIgnoreIdentifiers() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_IDENTIFIERS).toString());
	}

	public void setIgnoreIdentifiers(boolean ignoreIdentifiers) {
		options.setOption(Option.IGNORE_IDENTIFIERS, ignoreIdentifiers);
	}

	public boolean isIgnoreStrings() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_STRINGS).toString());
	}

	public void setIgnoreStrings(boolean ignoreStrings) {
		options.setOption(Option.IGNORE_STRINGS, ignoreStrings);
	}

	public boolean isIgnoreNumbers() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_NUMBERS).toString());
	}

	public void setIgnoreNumbers(boolean ignoreNumbers) {
		options.setOption(Option.IGNORE_NUMBERS, ignoreNumbers);
	}

	public boolean isIgnoreCharacters() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_CHARACTERS).toString());
	}

	public void setIgnoreCharacters(boolean ignoreCharacters) {
		options.setOption(Option.IGNORE_CHARACTERS, ignoreCharacters);
	}

	public boolean isIgnoreLiterals() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_LITERALS).toString());
	}

	public void setIgnoreLiterals(boolean ignoreLiterals) {
		options.setOption(Option.IGNORE_LITERALS, ignoreLiterals);
	}

	public boolean isIgnoreSubtypeNames() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_SUBTYPE_NAMES).toString());
	}

	public void setIgnoreSubtypeNames(boolean ignoreSubtypeNames) {
		options.setOption(Option.IGNORE_SUBTYPE_NAMES, ignoreSubtypeNames);
	}

	public boolean isIgnoreModifiers() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_MODIFIERS).toString());
	}

	public void setIgnoreModifiers(boolean ignoreModifiers) {
		options.setOption(Option.IGNORE_MODIFIERS, ignoreModifiers);
	}

	public boolean isIgnoreVariableNames() {
		return Boolean.parseBoolean(options.getOption(Option.IGNORE_VARIABLE_NAMES).toString());
	}

	public void setIgnoreVariableNames(boolean ignoreVariableNames) {
		options.setOption(Option.IGNORE_VARIABLE_NAMES, ignoreVariableNames);
	}
}
