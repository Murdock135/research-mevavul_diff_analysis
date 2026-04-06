class doGet {
public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		log.debug("doGet({}, {})", request, response);
		String uuid = WebUtils.getString(request, "uuid");
		String core = WebUtils.getString(request, "core");
		String theme = WebUtils.getString(request, "theme");
		InputStream fis = null;

		try {
			fis = OKMDocument.getInstance().getContent(null, uuid, false);
			StringWriter writer = new StringWriter();
			IOUtils.copy(fis, writer, "UTF-8");
			String content = writer.getBuffer().toString();
			content = content.replaceAll("jsOpenPathByUuid", "parent.jsOpenPathByUuid");

			ServletContext sc = getServletContext();
			sc.setAttribute("cssCore", core);
			sc.setAttribute("cssTheme", theme);
			sc.setAttribute("content", content);
			sc.getRequestDispatcher("/html_preview.jsp").forward(request, response);
		} catch (PathNotFoundException | AccessDeniedException | RepositoryException | DatabaseException e) {
			sendErrorRedirect(request, response, e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
}
