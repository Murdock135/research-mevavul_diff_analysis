class doPost_1 {
protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// to get values from the login page
		String userName = request.getParameter("aname");
		String password = request.getParameter("pass");
		// String password = request.getParameter("pass");
		String rememberMe = request.getParameter("remember-me");

		// validation

		if (adminDao.loginValidate(userName, password)) {

			if (rememberMe != null) {
				Cookie cookie1 = new Cookie("uname", userName);
				Cookie cookie2 = new Cookie("pass", password);

				cookie1.setMaxAge(24 * 60 * 60);
				cookie2.setMaxAge(24 * 60 * 60);

				response.addCookie(cookie1);
				response.addCookie(cookie2);
			}

			// to display the name of logged-in person in home page
			HttpSession session = request.getSession();
			session.setAttribute("username", userName);

			/*
			 * RequestDispatcher rd =
			 * request.getRequestDispatcher("AdminController?actions=admin_list");
			 * rd.forward(request, response);
			 */

			response.sendRedirect("AdminController?actions=admin_list");
		} else {
			RequestDispatcher rd = request.getRequestDispatcher("adminlogin.jsp");
			request.setAttribute("loginFailMsg", "Invalid Username or Password !!");
			rd.include(request, response);
		}
	}
}
