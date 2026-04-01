class doFilter {
@Override
	 public void doFilter(ServletRequest srequest, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

/*	 		HttpServletRequest request = (HttpServletRequest) srequest;
	 		//final String realIp = request.getHeader(X_FORWARDED_FOR);

	 		//if (realIp != null) {
	 			filterChain.doFilter(new XssHttpServletRequestWrapper(request) {
	 				*//**
	 				public String getRemoteAddr() {
	 					return realIp;
	 				}

	 				public String getRemoteHost() {
	 					return realIp;
	 				}
	 				**//*
	 			}, response);

	 			return;
	 		//}

*/

	 }
}
