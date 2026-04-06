class decrypt {
public static Secret decrypt(String data) {
        if(data==null)      return null;
        try {
            byte[] in = Base64.decode(data.toCharArray());
            Secret s = tryDecrypt(KEY.decrypt(), in);
            if (s!=null)    return s;

            // try our historical key for backward compatibility
            Cipher cipher = getCipher("AES");
            cipher.init(Cipher.DECRYPT_MODE, getLegacyKey());
            return tryDecrypt(cipher, in);
        } catch (GeneralSecurityException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            throw new Error(e); // impossible
        } catch (IOException e) {
            return null;
        }
    }
}
