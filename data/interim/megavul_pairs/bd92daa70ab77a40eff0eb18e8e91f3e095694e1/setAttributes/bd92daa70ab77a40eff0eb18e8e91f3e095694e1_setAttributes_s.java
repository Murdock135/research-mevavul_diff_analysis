class setAttributes {
@Override
	protected void setAttributes(HttpServletRequest request) {
		request.setAttribute("liferay-ui:header:backLabel", _backLabel);

		String redirect = ParamUtil.getString(request, "redirect");

		if (Validator.isNull(_backURL) && Validator.isNotNull(redirect)) {
			request.setAttribute("liferay-ui:header:backURL", redirect);
		}
		else {
			request.setAttribute("liferay-ui:header:backURL", _backURL);
		}

		request.setAttribute("liferay-ui:header:cssClass", _cssClass);
		request.setAttribute(
			"liferay-ui:header:escapeXml", String.valueOf(_escapeXml));
		request.setAttribute(
			"liferay-ui:header:localizeTitle", String.valueOf(_localizeTitle));
		request.setAttribute(
			"liferay-ui:header:showBackURL", String.valueOf(_showBackURL));
		request.setAttribute("liferay-ui:header:title", _title);
	}
}
