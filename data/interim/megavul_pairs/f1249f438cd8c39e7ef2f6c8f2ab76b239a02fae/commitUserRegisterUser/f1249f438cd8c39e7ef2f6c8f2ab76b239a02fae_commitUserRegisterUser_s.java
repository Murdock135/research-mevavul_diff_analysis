class commitUserRegisterUser {
public ModelAndView commitUserRegisterUser() {
		String username = "";
		String password = "";
		String confirmPassword = "";
		String email = "";
		String confirmEmail = "";
		String avatarPath = "";
		try{
			username = this.getValue(request.getPart("username"));
			password = this.getValue(request.getPart("password"));
			confirmPassword = this.getValue(request.getPart("confirmPassword"));
			email = this.getValue(request.getPart("email"));
			confirmEmail = this.getValue(request.getPart("confirmEmail"));
			avatarPath = FileUploadController.getFileName(request.getPart("image"));
		} catch (ServletException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		RegisterUserModel model = new RegisterUserModel();
		ModelAndView mv = null;
		
		if(!password.equals(confirmPassword)) {
			model.setErrorMessage("Bad username/password. ");
			request.setAttribute("attemptedAccount", new Account(username, email, avatarPath, Roles.User, password));
			mv = new ModelAndView(model, "/WEB-INF/register.jsp");
		}
		if(!email.equals(confirmEmail)){
			model.setErrorMessage(model.getErrorMessage() + "Emails did not match. ");
			request.setAttribute("attemptedAccount", new Account(username, email, avatarPath, Roles.User, password));
			mv = new ModelAndView(model, "/WEB-INF/register.jsp");
		}
		try {
			Account user = new Account(username, email, avatarPath, Roles.User, password);
			dataService.registerUser(user);
			FileUploadController.processRequest(request, response, filePath);
			model.setUser(user);
			mv = new ModelAndView(model, "/WEB-INF/account/profile.jsp");
		} catch(UsernameAlreadyExistsException e) {
			request.setAttribute("attemptedAccount", new Account(username, email, avatarPath, Roles.User, password));
			model.setErrorMessage("Username has already been used.");
			mv = new ModelAndView(model, "/WEB-INF/register.jsp");
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mv;
	}
}
