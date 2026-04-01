class clean {
private String clean(final String svg) {
		final String svg2 = svg.toLowerCase().replaceAll("\\s", "");
		if (svg2.contains("<script>"))
			return EMPTY_SVG;
		if (svg2.contains("</script>"))
			return EMPTY_SVG;
		if (svg2.contains("<foreignobject"))
			return EMPTY_SVG;
		if (svg2.contains("</foreignobject>"))
			return EMPTY_SVG;
		return svg;
	}
}
