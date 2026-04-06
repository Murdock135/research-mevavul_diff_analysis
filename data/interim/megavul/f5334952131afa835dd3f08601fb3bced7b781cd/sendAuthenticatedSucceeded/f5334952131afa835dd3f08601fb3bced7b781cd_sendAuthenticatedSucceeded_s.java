class sendAuthenticatedSucceeded {
private void sendAuthenticatedSucceeded(Fingerprint fp) {
            if (mAuthenticationCallback != null) {
                final AuthenticationResult result = new AuthenticationResult(mCryptoObject, fp);
                mAuthenticationCallback.onAuthenticationSucceeded(result);
            }
        }
}
