class checkIfWeAreAuthenticated {
@RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkIfWeAreAuthenticated(String screenLockTimeout) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(CREDENTIALS_KEY, null);
            Cipher cipher =
                    Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_GCM + "/" + KeyProperties.ENCRYPTION_PADDING_NONE);

            // Try encrypting something, it will only work if the user authenticated within
            // the last AUTHENTICATION_DURATION_SECONDS seconds.
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cipher.doFinal(SECRET_BYTE_ARRAY);

            cryptoObject = new BiometricPrompt.CryptoObject(cipher);
            // If the user has recently authenticated, we will reach here
            return true;
        } catch (UserNotAuthenticatedException e) {
            // User is not authenticated, let's authenticate with device credentials.
            return false;
        } catch (KeyPermanentlyInvalidatedException e) {
            // This happens if the lock screen has been disabled or reset after the key was
            // generated.
            // Shouldn't really happen because we regenerate the key every time an activity
            // is created, but oh well
            // Create key, and attempt again
            createKey(screenLockTimeout);
            return false;
        } catch (BadPaddingException | IllegalBlockSizeException | KeyStoreException |
                CertificateException | UnrecoverableKeyException | IOException
                | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }
}
