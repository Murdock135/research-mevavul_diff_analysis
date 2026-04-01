class escape {
private static String escape(String value) {
		boolean quoteIt = false;
		StringBuilder fixedValue = new StringBuilder(value.length() + 20);

		for (char c : value.toCharArray()) {
			switch (c) {
				case '\n':
					fixedValue.append("\\n");
					break;

				case '\t':
					fixedValue.append("\\t");
					break;

				case '\b':
					fixedValue.append("\\b");
					break;

				case '\\':
					fixedValue.append("\\\\");
					break;

				case '"':
					fixedValue.append("\\\"");
					break;

				case ';':
				case '#':
					quoteIt = true;
					fixedValue.append(c);
					break;

				default:
					fixedValue.append(c);
					break;
			}
		}

		if (quoteIt) {
			fixedValue.insert(0,"\"");
			fixedValue.append("\"");
		}
		return fixedValue.toString();
	}
}
