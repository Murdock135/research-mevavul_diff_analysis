class sendAuthenticatedSucceeded {
private void sendAuthenticatedSucceeded(Fingerprint fp, int userId) {
            if (mAuthenticationCallback != null) {
                final AuthenticationResult result =
                        new AuthenticationResult(mCryptoObject, fp, userId);
                mAuthenticationCallback.onAuthenticationSucceeded(result);
            }
        }
}
