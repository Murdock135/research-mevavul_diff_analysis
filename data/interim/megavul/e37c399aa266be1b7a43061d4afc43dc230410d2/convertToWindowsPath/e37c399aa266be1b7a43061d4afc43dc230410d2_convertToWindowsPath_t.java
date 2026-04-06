class convertToWindowsPath {
public static String convertToWindowsPath(String pathString) {
		String pathSlash = getFilePathSlash(pathString);
		if (pathString.startsWith(pathSlash)) {
			if (pathString.length() > 3) {
				char letter = pathString.charAt(1);
				char colon = pathString.charAt(2);
				if (Character.isLetter(letter) && ':' == colon) {
					pathString = pathString.substring(1);
				}
			}
		}
		return pathString.replace("/", "\\");
	}
}
