class escape {
private static String escape(String value) {
		String fixedValue = '#' == value.charAt(0) ? "\"" + value + "\"" : value;
		fixedValue = fixedValue.replace("\\", "\\\\");
		return fixedValue;
	}
}
