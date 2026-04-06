class sendErrorRedirect {
protected void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response, Throwable e) throws
			ServletException, IOException {
		request.setAttribute("javax.servlet.jsp.jspException", e);
		ServletContext sc = getServletConfig().getServletContext();
		sc.getRequestDispatcher("/error.jsp").forward(request, response);
	}
}
