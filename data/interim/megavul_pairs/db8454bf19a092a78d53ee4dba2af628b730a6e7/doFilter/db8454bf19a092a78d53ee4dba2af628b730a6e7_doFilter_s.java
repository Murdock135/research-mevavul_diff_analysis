class doFilter {
@Override
	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
		if (skipFilter((HttpServletRequest) request)) {
			chain.doFilter(request, response);
		} else {
			
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			
			String servletPath = httpRequest.getServletPath();
			// for all /images and /initfilter/scripts files, write the path
			// (the "/initfilter" part is needed so that the openmrs_static_context-servlet.xml file doesn't
			//  get instantiated early, before the locale messages are all set up)
			if (servletPath.startsWith("/images") || servletPath.startsWith("/initfilter/scripts")) {
				servletPath = servletPath.replaceFirst("/initfilter", "/WEB-INF/view"); // strip out the /initfilter part
				// writes the actual image file path to the response
				File file = new File(filterConfig.getServletContext().getRealPath(servletPath));
				if (httpRequest.getPathInfo() != null) {
					file = new File(file, httpRequest.getPathInfo());
				}
				
				InputStream imageFileInputStream = null;
				try {
					imageFileInputStream = new FileInputStream(file);
					OpenmrsUtil.copyFile(imageFileInputStream, httpResponse.getOutputStream());
				}
				catch (FileNotFoundException e) {
					log.error("Unable to find file: " + file.getAbsolutePath());
				}
				finally {
					if (imageFileInputStream != null) {
						try {
							imageFileInputStream.close();
						}
						catch (IOException io) {
							log.warn("Couldn't close imageFileInputStream: " + io);
						}
					}
				}
			} else if (servletPath.startsWith("/scripts")) {
				log.error(
				    "Calling /scripts during the initializationfilter pages will cause the openmrs_static_context-servlet.xml to initialize too early and cause errors after startup.  Use '/initfilter"
				            + servletPath + "' instead.");
			}
			// for anything but /initialsetup
			else if (!httpRequest.getServletPath().equals("/" + WebConstants.SETUP_PAGE_URL)
			        && !httpRequest.getServletPath().equals("/" + AUTO_RUN_OPENMRS)) {
				// send the user to the setup page
				httpResponse.sendRedirect("/" + WebConstants.WEBAPP_NAME + "/" + WebConstants.SETUP_PAGE_URL);
			} else {
				
				if ("GET".equals(httpRequest.getMethod())) {
					doGet(httpRequest, httpResponse);
				} else if ("POST".equals(httpRequest.getMethod())) {
					// only clear errors before POSTS so that redirects can show errors too.
					errors.clear();
					msgs.clear();
					doPost(httpRequest, httpResponse);
				}
			}
			// Don't continue down the filter chain otherwise Spring complains
			// that it hasn't been set up yet.
			// The jsp and servlet filter are also on this chain, so writing to
			// the response directly here is the only option
		}
	}
}
