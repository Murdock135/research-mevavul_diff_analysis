class isValidImageCaptcha {
public static boolean isValidImageCaptcha(HttpServletRequest request){

		HttpSession session = request.getSession();
		String captcha = request.getParameter("captcha");
		Captcha captchaObj = (Captcha) session.getAttribute(Captcha.NAME);
        String captchaSession=captchaObj!=null ? captchaObj.getAnswer() : null;
		if(!UtilMethods.isSet(captcha) || !UtilMethods.isSet(captchaSession) || !captcha.equals(captchaSession)){
			return false;
		}
		return true;

	}
}
