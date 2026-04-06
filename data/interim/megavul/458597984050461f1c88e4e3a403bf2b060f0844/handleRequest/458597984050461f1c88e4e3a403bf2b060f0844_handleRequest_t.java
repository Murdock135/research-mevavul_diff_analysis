class handleRequest {
@RequestMapping("/module/htmlformentry/htmlFormFromFile.form")
	public void handleRequest(Model model, @RequestParam(value = "filePath", required = false) String filePath,
	        @RequestParam(value = "patientId", required = false) Integer pId,
	        @RequestParam(value = "isFileUpload", required = false) boolean isFileUpload, HttpServletRequest request)
	        throws Exception {
		
		Context.requirePrivilege("Manage Forms");
		
		if (log.isDebugEnabled())
			log.debug("In reference data...");
		
		model.addAttribute("previewHtml", "");
		String message;
		File f = null;
		try {
			if (isFileUpload) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				MultipartFile multipartFile = multipartRequest.getFile("htmlFormFile");
				if (multipartFile != null) {
					// use an unpredictable file name
					f = File.createTempFile(TEMP_HTML_FORM_FILE_PREFIX, ".tmp");
					f.deleteOnExit();
					
					filePath = f.getAbsolutePath();
					FileOutputStream fileOut = new FileOutputStream(f);
					IOUtils.copy(multipartFile.getInputStream(), fileOut);
					fileOut.close();
				}
			} else {
				if (StringUtils.hasText(filePath)) {
					f = new File(filePath);
					// prevent reading  a file via an absolute path or path traversal
					if (f.isAbsolute() || !FilenameUtils.normalize(filePath).equals(filePath)) {
						f = null;
					}
				}
			}
			
			if (f != null && f.exists() && f.canRead()) {
				model.addAttribute("filePath", filePath);
				
				StringWriter writer = new StringWriter();
				IOUtils.copy(new FileInputStream(f), writer, "UTF-8");
				String xml = writer.toString();
				
				// validate file is actually xml
				HtmlFormEntryUtil.stringToDocument(xml);
				
				Patient p;
				if (pId != null) {
					p = Context.getPatientService().getPatient(pId);
				} else {
					p = HtmlFormEntryUtil.getFakePerson();
				}
				HtmlForm fakeForm = new HtmlForm();
				fakeForm.setXmlData(xml);
				FormEntrySession fes = new FormEntrySession(p, null, Mode.ENTER, fakeForm, request.getSession());
				String html = fes.getHtmlToDisplay();
				if (fes.getFieldAccessorJavascript() != null) {
					html += "<script>" + fes.getFieldAccessorJavascript() + "</script>";
				}
				model.addAttribute("previewHtml", html);
				//clear the error message
				message = "";
			} else {
				message = "Please specify a valid file path or select a valid file.";
			}
		}
		catch (Exception e) {
			log.error("An error occurred while loading the html.", e);
			message = "An error occurred while loading the html. " + e.getMessage();
		}
		
		model.addAttribute("message", message);
		model.addAttribute("isFileUpload", isFileUpload);
	}
}
