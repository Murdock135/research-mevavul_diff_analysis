class servlet31 {
private final void servlet31(HttpServletRequest request) {
		try {
			for(Part part:request.getParts()) {
				if(part.getContentType() != null && (StringHelper.containsNonWhitespace(part.getSubmittedFileName()) || !part.getContentType().startsWith("text/plain"))) {
					contentType = part.getContentType();
					filename = part.getSubmittedFileName();
					if(filename != null) {
						filename = UUID.randomUUID().toString().replace("-", "") + "_" + filename;
					} else {
						filename = "upload-" + UUID.randomUUID().toString().replace("-", "");
					}
					file = new File(WebappHelper.getTmpDir(), filename);
					part.write(file.getAbsolutePath());
					file = new File(WebappHelper.getTmpDir(), filename);
				} else {
					String value = IOUtils.toString(part.getInputStream(), request.getCharacterEncoding());
					fields.put(part.getName(), value);
				}
				
				try {
					part.delete();
				} catch (Exception e) {
					//we try (tomcat doesn't send exception but undertow)
				}
			}
		} catch (IOException | ServletException e) {
			log.error("", e);
		}
	}
}
