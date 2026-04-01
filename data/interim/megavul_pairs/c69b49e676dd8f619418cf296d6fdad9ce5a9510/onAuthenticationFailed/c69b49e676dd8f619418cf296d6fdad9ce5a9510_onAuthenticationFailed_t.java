class onAuthenticationFailed {
@Override
    public void onAuthenticationFailed() {
      Log.w(TAG, "onAuthenticationFailed()");

      fingerprintPrompt.setImageResource(R.drawable.ic_close_white_48dp);
      fingerprintPrompt.getBackground().setColorFilter(getResources().getColor(R.color.red_500), PorterDuff.Mode.SRC_IN);

      TranslateAnimation shake = new TranslateAnimation(0, 30, 0, 0);
      shake.setDuration(50);
      shake.setRepeatCount(7);
      shake.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
          fingerprintPrompt.setImageResource(R.drawable.ic_fingerprint_white_48dp);
          fingerprintPrompt.getBackground().setColorFilter(getResources().getColor(R.color.signal_primary), PorterDuff.Mode.SRC_IN);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
      });

      fingerprintPrompt.startAnimation(shake);
    }
}
