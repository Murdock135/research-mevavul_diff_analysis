class doPost {
@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getParameter("login") != null) {

				String username = request.getParameter("username");
				String password = request.getParameter("password");
				String isValidPwd = "";
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				
				if (username.isEmpty() || password.isEmpty()) {
					out.println("<html><head><body><p>A mandatory field is empty!<p></body></head>");
					System.out.println("EMPTY");
					return;
				}
				AddAppUser tmp = null;
				String query = "Select password from APPUSER where email=?";
//						+ username + "'";
				
				ArrayList<String> params=new ArrayList<String>();
				params.add(username);
				tmp = new AddAppUser(query,params);
				ResultSet res = tmp.addUser();
				
				if (res.next()) {
					isValidPwd = res.getString("password");
					
				}
				
				password=encryptPassword(password);
				
				if (password.equals(isValidPwd)) {
					//out.println("<html><head><body><h3>Login Success!!</h3></body></head>");
					response.sendRedirect("main.html");

				} else {
					System.out.println("Login Failed!!");
					out.println("<html><head><body><h3>Invalid credentials!!</h3></body></head>");
					out.flush();	
					tmp.closeDb();
					return;
				}
				tmp.closeDb();
			

			}
			if (request.getParameter("register") != null) {
				response.sendRedirect("register.html");

			}

			if (request.getParameter("signup") != null) {
				// boolean ifUserAlreadyRegisterd = true;

				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				String first = request.getParameter("firstname");
				String last = request.getParameter("lastname");
				String pwd = request.getParameter("password");
				String encrypt=encryptPassword(pwd);
				String email = request.getParameter("email");
				String isFbLogin = "N";

				if (first.isEmpty() || last.isEmpty() || pwd.isEmpty()
						|| email.isEmpty()) {

					out = response.getWriter();
					out.println("<html><head><body><p>A mandatory field is empty!<p></body></head>");
					// System.out.println("<html><head><body><p>A mandatory field is empty!<p></body></head>");

				} else {
					String query = "SELECT count(*) as cnt FROM APPUSER WHERE email=?";
//							+ email + "'";
					// System.out.println("Signup query" + query);
					ArrayList<String> params=new ArrayList<String>();
					params.add(email);
					AddAppUser add = new AddAppUser(query,params);
					ResultSet res = add.addUser();
					int count = 0;
					if (res.next()) {
						count = res.getInt("cnt");
					}
					
					add.closeDb();
					
					if (count > 0) {
						// System.out.println("Already Registered");
						out.println("<html><head><body><p>This email id is already registered!!</p></body></head>");
					} else {
						// System.out.println("Registering!");
						params=new ArrayList<String>();
						params.add(email);
						String query1 = "Insert into APPUSER(USER_ID,EMAIL,PASSWORD,FIRST_NAME,LAST_NAME,IS_FACEBOOK_LOGIN)"
								+ " values (usr_id.NEXTVAL,?"
//								+ email
								+ ",'"
								+ encrypt
								+ "','"
								+ first
								+ "','"
								+ last
								+ "','"
								+ isFbLogin + "')";
						// System.out.println(query1);
						AddAppUser tmp = new AddAppUser(query1,params);
						tmp.addUser();
						tmp.closeDb();
						response.sendRedirect("main.html");
						//out.println("<html><head><body><h3>Registration Successful!</h3></body></head>");
					}
				}

			}

		} catch (Exception e) {

		}
	}
}
