class get {
public void get(@RequestParam("encounter") Encounter encounter,
	        @RequestParam(value = "showPatientHeader", defaultValue = "true") boolean showPatientHeader,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        @RequestParam(value = "returnLabel", required = false) String returnLabel,
	        @RequestParam(value = "editStyle", defaultValue = "standard") String editStyle,
	        @InjectBeans PatientDomainWrapper patient,
	        @SpringBean("htmlFormEntryService") HtmlFormEntryService htmlFormEntryService,
	        @SpringBean("adminService") AdministrationService administrationService, UiUtils ui, PageModel model) {
		
		patient.setPatient(encounter.getPatient());
		
		String customPrintProvider = administrationService.getGlobalProperty("htmlformentryui.customPrintProvider");
		String customPrintPageName = administrationService.getGlobalProperty("htmlformentryui.customPrintPageName");
		String customPrintTarget = administrationService.getGlobalProperty("htmlformentryui.customPrintTarget");
		
		model.addAttribute("customPrintProvider", customPrintProvider);
		model.addAttribute("customPrintPageName", customPrintPageName);
		model.addAttribute("customPrintTarget", customPrintTarget);
		
		if (StringUtils.isEmpty(returnUrl)) {
			returnUrl = ui.pageLink("coreapps", "patientdashboard/patientDashboard",
			    SimpleObject.create("patientId", patient.getId()));
		}
		if (StringUtils.isEmpty(returnLabel)) {
			returnLabel = ui.escapeJs(ui.format(patient.getPatient()));
		}
		
		model.addAttribute("patient", patient);
		model.addAttribute("visit", encounter.getVisit());
		model.addAttribute("encounter", encounter);
		model.addAttribute("returnUrl", returnUrl);
		model.addAttribute("returnLabel", returnLabel);
		model.addAttribute("editStyle", fixCase(editStyle));
		model.addAttribute("showPatientHeader", showPatientHeader);
		
		HtmlForm htmlForm = htmlFormEntryService.getHtmlFormByForm(encounter.getForm());
		if (htmlForm == null) {
			throw new IllegalArgumentException("encounter.form is not an HTML Form: " + encounter.getForm());
		}
		model.addAttribute("htmlForm", htmlForm);
	}
}
