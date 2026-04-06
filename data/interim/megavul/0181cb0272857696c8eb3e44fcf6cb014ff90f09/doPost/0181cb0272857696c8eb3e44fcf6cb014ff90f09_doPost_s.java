class doPost {
protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// to get values from the login page
		// HttpSession session=request.getSession();
		PrintWriter out = response.getWriter();
		int min = 100000;
		int max = 999999;
		otp = 5432;
		Random r = new Random();
		otp = r.nextInt(max - min) + min;

		String userName = request.getParameter("uname");
		String password = sha.getSHA(request.getParameter("pass"));
		String vemail = request.getParameter("vmail");

		String recipient = vemail;
		String subject = "otp verification";
		String content = "your otp is: " + otp;
		// System.out.print(recipient);
		String resultMessage = "";

		// validation
		if (voterDao.loginValidate(userName, password, vemail)) {

			// to display the name of logged-in person in home page

			HttpSession session = request.getSession();
			session.setAttribute("username", userName);

			try {
				EmailSend.sendEmail(host, port, user, pass, recipient, subject, content);
			} catch (MessagingException e) {
				e.printStackTrace();
				resultMessage = "There were an error: " + e.getMessage();
			} finally {
				RequestDispatcher rd = request.getRequestDispatcher("OTP.jsp");
				rd.include(request, response);
				out.println("<script type=\"text/javascript\">");
				out.println("alert('" + resultMessage + "');");
				out.println("</script>");
			}

		} else {
			RequestDispatcher rd = request.getRequestDispatcher("voterlogin.jsp");
			request.setAttribute("loginFailMsg", "Invalid Input ! Enter again !!");
			// request.setAttribute("forgotPassMsg", "Forgot password??");
			rd.include(request, response);
			/*
			 * String forgetpass = request.getParameter("forgotPass"); //
			 * System.out.println(forgetpass); if (forgetpass == null) { rd =
			 * request.getRequestDispatcher("resetPassword.jsp"); rd.forward(request,
			 * response);
			 */
		}
	}
}
