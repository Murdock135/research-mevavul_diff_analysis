class validate {
@Override
	public void validate(Object obj, Errors errors) {
		HtmlForm hf = (HtmlForm) obj;
		// can't use ValidationUtil for this because toString of a new non-null form is ""
		if (hf.getForm() == null)
			errors.rejectValue("form", "error.null");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.null");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "xmlData", "error.null");
		
		if (hf.getForm() != null) {
			errors.pushNestedPath("form");
			new FormValidator().validate(hf.getForm(), errors);
			errors.popNestedPath();
		}
		if (hf.getXmlData() != null) {
			try {
				@SuppressWarnings("unused")
				FormEntrySession session = new FormEntrySession(HtmlFormEntryUtil.getFakePerson(), hf.getXmlData(), null); // can't access an HttpSession here
				if (hf.getForm() != null) {
					if (hf.getForm().getEncounterType() != null && hasEncounterTypeTag(hf.getXmlData())) {
						throw new FormEntryException(
						        "encounterType tag is not allowed for a form that is already associated to encounter type");
					}
				}
				HtmlFormEntryGenerator htmlGenerator = new HtmlFormEntryGenerator();
				String xml = hf.getXmlData();
				xml = htmlGenerator.substituteCharacterCodesWithAsciiCodes(xml);
				xml = htmlGenerator.stripComments(xml);
				xml = htmlGenerator.convertSpecialCharactersWithinLogicAndVelocityTests(xml);
				xml = htmlGenerator.applyRoleRestrictions(xml);
				xml = htmlGenerator.applyMacros(session, xml);
				xml = htmlGenerator.applyRepeats(xml);
				Document document = HtmlFormEntryUtil.stringToDocument(xml);
				validateTags(document, errors, null);
			}
			catch (Exception ex) {
				errors.rejectValue("xmlData", null, ex.getMessage());
				log.warn("Error in HTML form", ex);
			}
		}
	}
}
