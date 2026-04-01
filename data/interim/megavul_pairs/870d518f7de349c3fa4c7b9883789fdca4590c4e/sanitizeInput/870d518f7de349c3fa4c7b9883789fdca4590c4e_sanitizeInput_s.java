class sanitizeInput {
public static String sanitizeInput(String string) {
		return string
				.replaceAll("(?i)<script.*?>.*?</script.*?>", "") // case 1 - Open and close
				.replaceAll("(?i)<script.*?/>", "") // case 1 - Open / close
				.replaceAll("(?i)<script.*?>", "") // case 1 - Open and !close
				.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "") // case 2 - Open and close
				.replaceAll("(?i)<.*?javascript:.*?/>", "") // case 2 - Open / close
				.replaceAll("(?i)<.*?javascript:.*?>", "") // case 2 - Open and !close
				.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "") // case 3 - Open and close
				.replaceAll("(?i)<.*?\\s+on.*?/>", "") // case 3 - Open / close
				.replaceAll("(?i)<.*?\\s+on.*?>", ""); // case 3 - Open and !close
	}
}
