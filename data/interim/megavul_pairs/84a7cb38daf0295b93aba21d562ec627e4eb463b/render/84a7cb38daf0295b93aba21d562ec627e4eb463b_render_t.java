class render {
@Override
	public String render(String templateContent, Map<String, Object> context) throws TemplateRenderException {
		VelocityContext velocityContext = new VelocityContext(context);
		try (StringWriter sw = new StringWriter()) {
			Velocity.evaluate(velocityContext, sw, "velocityTemplateEngine", templateContent);
			return sw.toString();
		}
		catch (Exception ex) {
			throw new TemplateRenderException(ex);
		}
	}
}
