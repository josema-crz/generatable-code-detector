package utils;

public class Logger {
	private static final boolean activated = true;

	public static void log(String text) {
		if (activated) {
			System.out.println("LOG---: " + text);
		}
	}

	public static boolean isActivated() {
		return activated;
	}
}
