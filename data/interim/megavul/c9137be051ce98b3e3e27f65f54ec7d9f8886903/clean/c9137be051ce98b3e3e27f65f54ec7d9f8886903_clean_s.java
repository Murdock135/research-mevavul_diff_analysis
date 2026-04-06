class clean {
private String clean(String svg) {
		svg = svg.toLowerCase().replaceAll("\\s", "");
		if (svg.contains("<script>"))
			return EMPTY_SVG;
		if (svg.contains("</script>"))
			return EMPTY_SVG;
		if (svg.contains("<foreignobject"))
			return EMPTY_SVG;
		if (svg.contains("</foreignobject>"))
			return EMPTY_SVG;
		return svg;
	}
}
