class onAuthenticationSucceeded {
@Override
        public void onAuthenticationSucceeded(AuthenticationResult result) {
            handleFingerprintAuthenticated(result.getUserId());
        }
}
