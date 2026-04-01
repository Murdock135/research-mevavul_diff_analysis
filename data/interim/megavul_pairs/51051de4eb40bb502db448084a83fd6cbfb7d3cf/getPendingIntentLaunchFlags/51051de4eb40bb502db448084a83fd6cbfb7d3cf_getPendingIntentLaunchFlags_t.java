class getPendingIntentLaunchFlags {
public int getPendingIntentLaunchFlags() {
        // b/243794108: Ignore all flags except the new task flag, to be reconsidered in b/254490217
        return mPendingIntentLaunchFlags &
                (FLAG_ACTIVITY_NEW_TASK | FLAG_RECEIVER_FOREGROUND);
    }
}
