class hasPrivilegedAccess {
private boolean hasPrivilegedAccess(int callingUid, ActivityInfo activityInfo) {
        if (TextUtils.equals(PasswordUtils.getCallingAppPackageName(getActivityToken()),
                    getPackageName())) {
            return true;
        }

        int targetUid = -1;
        try {
            targetUid = getPackageManager().getApplicationInfo(activityInfo.packageName,
                    /* flags= */ 0).uid;
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(TAG, "Not able to get targetUid: " + nnfe);
            return false;
        }

        // When activityInfo.exported is false, Activity still can be launched if applications have
        // the same user ID.
        if (UserHandle.isSameApp(callingUid, targetUid)) {
            return true;
        }

        // When activityInfo.exported is false, Activity still can be launched if calling app has
        // root or system privilege.
        int callingAppId = UserHandle.getAppId(callingUid);
        if (callingAppId == Process.ROOT_UID || callingAppId == Process.SYSTEM_UID) {
            return true;
        }

        return false;
    }
}
