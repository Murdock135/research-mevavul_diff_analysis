class startSipService {
boolean startSipService(SipManager sipManager, Context context, boolean isReceivingCalls) {
            if (VERBOSE) log("startSipService, profile: " + mProfile);
            try {
                // Stop the Sip service for the profile if it is already running.  This is important
                // if we are changing the state of the "receive calls" option.
                sipManager.close(mProfile.getUriString());

                // Start the sip service for the profile.
                if (isReceivingCalls) {
                    sipManager.open(
                            mProfile,
                            SipUtil.createIncomingCallPendingIntent(context,
                                    mProfile.getUriString()),
                            null);
                } else {
                    sipManager.open(mProfile);
                }
                return true;
            } catch (SipException e) {
                log("startSipService, profile: " + mProfile.getProfileName() +
                        ", exception: " + e);
            }
            return false;
        }
}
