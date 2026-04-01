class isValidAudioCaptcha {
public static boolean isValidAudioCaptcha(HttpServletRequest request){

		HttpSession session = request.getSession();
		Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
		String captchaSession=captcha!=null ? captcha.getAnswer() : null;
		Boolean isResponseCorrect = Boolean.FALSE;
		String captchaId = request.getSession().getId();
		String audioCaptcha = request.getParameter("captcha");

		if(UtilMethods.isSet(audioCaptcha) && UtilMethods.isSet(captchaSession) && audioCaptcha.equals(captchaSession)){
			isResponseCorrect = Boolean.TRUE;
			session.removeAttribute(Captcha.NAME);

		}else if(UtilMethods.isSet(audioCaptcha) && UtilMethods.isSet(captchaId)){

			SoundCaptchaService soundCaptchaService = (SoundCaptchaService)session.getAttribute(WebKeys.SESSION_JCAPTCHA_SOUND_SERVICE);

			try {
				isResponseCorrect = soundCaptchaService.validateResponseForID(captchaId, audioCaptcha);

			} catch (CaptchaServiceException e) {
				Logger.error(CaptchaUtil.class, "An error ocurred trying to validate audio captcha", e);
			}
		}

		return isResponseCorrect;

	}
}
