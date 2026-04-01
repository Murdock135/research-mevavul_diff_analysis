class getSHA {
public String getSHA(String password) {
		try {
			byte[] salt = getSalt();
			String cipher = getCipher(password, salt);
			
			return cipher;

			// For specifying wrong message digest algorithms
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
