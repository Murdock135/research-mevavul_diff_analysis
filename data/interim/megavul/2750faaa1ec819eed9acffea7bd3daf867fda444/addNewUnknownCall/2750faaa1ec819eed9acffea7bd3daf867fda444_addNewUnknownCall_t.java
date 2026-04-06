class addNewUnknownCall {
@Override
        public void addNewUnknownCall(PhoneAccountHandle phoneAccountHandle, Bundle extras) {
            synchronized (mLock) {
                if (phoneAccountHandle != null && phoneAccountHandle.getComponentName() != null) {
                    mAppOpsManager.checkPackage(
                            Binder.getCallingUid(),
                            phoneAccountHandle.getComponentName().getPackageName());

                    // Make sure it doesn't cross the UserHandle boundary
                    enforceUserHandleMatchesCaller(phoneAccountHandle);
                    enforcePhoneAccountIsRegisteredEnabled(phoneAccountHandle);
                    long token = Binder.clearCallingIdentity();

                    try {
                        Intent intent = new Intent(TelecomManager.ACTION_NEW_UNKNOWN_CALL);
                        intent.putExtras(extras);
                        intent.putExtra(CallIntentProcessor.KEY_IS_UNKNOWN_CALL, true);
                        intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,
                            phoneAccountHandle);
                        CallIntentProcessor.processUnknownCallIntent(mCallsManager, intent);
                    } finally {
                        Binder.restoreCallingIdentity(token);
                    }
                } else {
                    Log.i(this,
                            "Null phoneAccountHandle or not initiated by Telephony. " +
                            "Ignoring request to add new unknown call.");
                }
            }
        }
}
