class getEncryptedValue {
public String getEncryptedValue() {
        try {
            Cipher cipher = KEY.encrypt();
            // add the magic suffix which works like a check sum.
            return new String(Base64.encode(cipher.doFinal((value+MAGIC).getBytes("UTF-8"))));
        } catch (GeneralSecurityException e) {
            throw new Error(e); // impossible
        } catch (UnsupportedEncodingException e) {
            throw new Error(e); // impossible
        }
    }
}
