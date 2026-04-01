class resumeScreenLock {
private void resumeScreenLock() {
    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

    assert keyguardManager != null;

    if (!keyguardManager.isKeyguardSecure()) {
      Log.w(TAG ,"Keyguard not secure...");
      TextSecurePreferences.setScreenLockEnabled(getApplicationContext(), false);
      TextSecurePreferences.setScreenLockTimeout(getApplicationContext(), 0);
      handleAuthenticated();
      return;
    }

    if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
      Log.i(TAG, "Listening for fingerprints...");
      fingerprintCancellationSignal = new CancellationSignal();
      fingerprintManager.authenticate(null, 0, fingerprintCancellationSignal, fingerprintListener, null);
    } else {
      Log.i(TAG, "firing intent...");
      Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("Unlock Session", "");
      startActivityForResult(intent, 1);
    }
  }
}
