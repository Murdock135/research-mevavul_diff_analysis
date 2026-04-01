class onAuthenticationSucceeded {
@Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
      Log.i(TAG, "onAuthenticationSucceeded");
      if (result.getCryptoObject() == null || result.getCryptoObject().getSignature() == null) {
        // authentication failed
        onAuthenticationFailed();
        return;
      }
      // Signature object now successfully unlocked
      boolean authenticationSucceeded = false;
      try {
        Signature signature = result.getCryptoObject().getSignature();
        byte[] random = biometricSecretProvider.getRandomData();
        signature.update(random);
        byte[] signed = signature.sign();
        authenticationSucceeded = biometricSecretProvider.verifySignature(random, signed);
      } catch (Exception e) {
        Log.e(TAG, "onAuthentication signature generation and verification failed", e);
      }
      if (!authenticationSucceeded) {
        onAuthenticationFailed();
        return;
      }

      fingerprintPrompt.setImageResource(R.drawable.ic_check_white_48dp);
      fingerprintPrompt.getBackground().setColorFilter(getResources().getColor(R.color.green_500), PorterDuff.Mode.SRC_IN);
      fingerprintPrompt.animate().setInterpolator(new BounceInterpolator()).scaleX(1.1f).scaleY(1.1f).setDuration(500).setListener(new AnimationCompleteListener() {
        @Override
        public void onAnimationEnd(Animator animation) {
          handleAuthenticated();

          fingerprintPrompt.setImageResource(R.drawable.ic_fingerprint_white_48dp);
          fingerprintPrompt.getBackground().setColorFilter(getResources().getColor(R.color.signal_primary), PorterDuff.Mode.SRC_IN);
        }
      }).start();
    }
}
