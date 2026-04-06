class getResourceType {
protected RuntimeResourceDefinition getResourceType(HomeRequest theRequest, HttpServletRequest theReq) throws ServletException {
		String resourceName = sanitizeUrlPart(defaultString(theReq.getParameter(PARAM_RESOURCE)));
		RuntimeResourceDefinition def = getContext(theRequest).getResourceDefinition(resourceName);
		if (def == null) {
			throw new ServletException("Invalid resourceName: " + resourceName);
		}
		return def;
	}
}
