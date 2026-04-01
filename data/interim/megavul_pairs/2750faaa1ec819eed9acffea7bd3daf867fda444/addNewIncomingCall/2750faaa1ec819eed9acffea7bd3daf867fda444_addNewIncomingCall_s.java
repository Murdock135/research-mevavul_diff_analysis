class addNewIncomingCall {
@Override
        public void addNewIncomingCall(PhoneAccountHandle phoneAccountHandle, Bundle extras) {
            synchronized (mLock) {
                Log.i(this, "Adding new incoming call with phoneAccountHandle %s",
                        phoneAccountHandle);
                if (phoneAccountHandle != null && phoneAccountHandle.getComponentName() != null) {
                    // TODO(sail): Add unit tests for adding incoming calls from a SIM call manager.
                    if (isCallerSimCallManager() && TelephonyUtil.isPstnComponentName(
                            phoneAccountHandle.getComponentName())) {
                        Log.v(this, "Allowing call manager to add incoming call with PSTN handle");
                    } else {
                        mAppOpsManager.checkPackage(
                                Binder.getCallingUid(),
                                phoneAccountHandle.getComponentName().getPackageName());
                        // Make sure it doesn't cross the UserHandle boundary
                        enforceUserHandleMatchesCaller(phoneAccountHandle);
                    }

                    long token = Binder.clearCallingIdentity();
                    try {
                        Intent intent = new Intent(TelecomManager.ACTION_INCOMING_CALL);
                        intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,
                            phoneAccountHandle);
                        intent.putExtra(CallIntentProcessor.KEY_IS_INCOMING_CALL, true);
                        if (extras != null) {
                            intent.putExtra(TelecomManager.EXTRA_INCOMING_CALL_EXTRAS, extras);
                        }
                        CallIntentProcessor.processIncomingCallIntent(mCallsManager, intent);
                    } finally {
                        Binder.restoreCallingIdentity(token);
                    }
                } else {
                    Log.w(this,
                            "Null phoneAccountHandle. Ignoring request to add new incoming call");
                }
            }
        }
}
