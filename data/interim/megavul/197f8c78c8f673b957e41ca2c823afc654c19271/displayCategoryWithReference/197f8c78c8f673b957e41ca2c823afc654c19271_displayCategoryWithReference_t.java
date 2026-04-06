class displayCategoryWithReference {
@RequestMapping("/shop/category/{friendlyUrl}.html/ref={ref}")
	public String displayCategoryWithReference(@PathVariable final String friendlyUrl, @PathVariable final String ref, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		return this.displayCategory(friendlyUrl,ref,model,request,response,locale);
	}
}
