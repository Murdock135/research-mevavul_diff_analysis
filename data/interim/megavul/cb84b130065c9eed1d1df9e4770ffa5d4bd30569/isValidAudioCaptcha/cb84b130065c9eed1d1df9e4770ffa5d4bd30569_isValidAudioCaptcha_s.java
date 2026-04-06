class isValidAudioCaptcha {
public static boolean isValidAudioCaptcha(HttpServletRequest request){

		Boolean isResponseCorrect =Boolean.FALSE;
		String captchaId = request.getSession().getId();  
		String audioCaptcha = request.getParameter("audioCaptcha");
		
		if(UtilMethods.isSet(audioCaptcha) && UtilMethods.isSet(captchaId)){
			try {
				//isResponseCorrect = CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId, audioCaptcha);
				
				SoundCaptchaService soundCaptchaService = (SoundCaptchaService) request.getSession().getAttribute(WebKeys.SESSION_JCAPTCHA_SOUND_SERVICE);
				isResponseCorrect = soundCaptchaService.validateResponseForID(captchaId, audioCaptcha);
				request.getSession().removeAttribute(WebKeys.SESSION_JCAPTCHA_SOUND_SERVICE);
			} catch (CaptchaServiceException e) {
				Logger.error(CaptchaUtil.class, "An error ocurred trying to validate audio captcha", e);
			}
		}

		return isResponseCorrect;

	}
}
