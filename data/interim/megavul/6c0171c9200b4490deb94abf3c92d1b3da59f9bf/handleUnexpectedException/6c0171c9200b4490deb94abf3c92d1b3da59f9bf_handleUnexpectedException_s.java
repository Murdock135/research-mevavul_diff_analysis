class handleUnexpectedException {
private boolean handleUnexpectedException(GFile file, Exception e) {
		errorredFiles.put(file.getFSRL(), e.getMessage());

		if (skipAllErrors) {
			return true;
		}

		int option = OptionDialog.showOptionDialog(parentComponent, "Error Extracting File",
			"There was a problem copying file " + file.getPath() + "\n\n" + e.getMessage() +
				"\n\nSkip this file and continue or cancel entire operation?",
			"Skip && Continue", "Skip All");
		if (option == OptionDialog.OPTION_TWO /* Skip All */) {
			skipAllErrors = true;
		}

		if (!skipAllErrors && option != OptionDialog.OPTION_ONE /* ie. != Skip this one*/) {
			return false;
		}
		return true;
	}
}
