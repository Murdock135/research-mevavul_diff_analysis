class determineReturnLabel {
public static String determineReturnLabel(String returnLabel, Patient patient, UiUtils ui) {
		
		if (org.apache.commons.lang.StringUtils.isNotBlank(returnLabel)) {
			return ui.message(returnLabel);
		} else {
			return ui.escapeJs(ui.format(patient));
		}
		
	}
}
