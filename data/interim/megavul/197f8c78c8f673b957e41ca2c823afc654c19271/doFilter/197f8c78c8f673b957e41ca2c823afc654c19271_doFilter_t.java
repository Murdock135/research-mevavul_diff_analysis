class doFilter {
@Override
	 public void doFilter(ServletRequest srequest, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

	 		HttpServletRequest request = (HttpServletRequest) srequest;
	 		filterChain.doFilter(new XssHttpServletRequestWrapper(request) {}, response);

	 }
}
