class onAuthenticationSucceeded {
@Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
      Log.i(TAG, "onAuthenticationSucceeded");
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
