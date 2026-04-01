class setMediaButtonBroadcastReceiver {
@Override
        @RequiresPermission(Manifest.permission.INTERACT_ACROSS_USERS)
        public void setMediaButtonBroadcastReceiver(ComponentName receiver) throws RemoteException {
            final long token = Binder.clearCallingIdentity();
            try {
                //mPackageName has been verified in MediaSessionService.enforcePackageName().
                if (receiver != null && !TextUtils.equals(
                        mPackageName, receiver.getPackageName())) {
                    EventLog.writeEvent(0x534e4554, "238177121", -1, ""); // SafetyNet logging.
                    throw new IllegalArgumentException("receiver does not belong to "
                            + "package name provided to MediaSessionRecord. Pkg = " + mPackageName
                            + ", Receiver Pkg = " + receiver.getPackageName());
                }
                if ((mPolicies & MediaSessionPolicyProvider.SESSION_POLICY_IGNORE_BUTTON_RECEIVER)
                        != 0) {
                    return;
                }

                if (!componentNameExists(receiver, mContext, mUserId)) {
                    Log.w(
                            TAG,
                            "setMediaButtonBroadcastReceiver(): "
                                    + "Ignoring invalid component name="
                                    + receiver);
                    return;
                }

                mMediaButtonReceiverHolder = MediaButtonReceiverHolder.create(mUserId, receiver);
                mService.onMediaButtonReceiverChanged(MediaSessionRecord.this);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }
}
