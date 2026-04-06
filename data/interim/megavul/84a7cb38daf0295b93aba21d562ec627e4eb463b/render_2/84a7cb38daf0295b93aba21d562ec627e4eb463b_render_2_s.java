class render_2 {
public String render(TemplateEngineTypeEnum engineType, String templateContent, Map<String, Object> context) {
		if (StrUtil.isEmpty(templateContent)) {
			return StrUtil.EMPTY;
		}
		TemplateEngine templateEngine = templateEngineMap.get(engineType);
		Assert.notNull(templateEngine, "未找到对应的模板引擎：{}", engineType);
		return templateEngine.render(templateContent, context);
	}
}
