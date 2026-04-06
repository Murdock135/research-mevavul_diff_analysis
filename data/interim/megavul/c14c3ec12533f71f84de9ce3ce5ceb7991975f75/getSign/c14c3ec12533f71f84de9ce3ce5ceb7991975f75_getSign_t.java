class getSign {
private String getSign(Long timestamp) {
		try {
			String stringToSign = timestamp + "\n" + secret;
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
			return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
}
