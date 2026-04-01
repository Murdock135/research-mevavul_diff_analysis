class hasPrivilegedAccess {
private boolean hasPrivilegedAccess(String callerPkg, int callerUid, String targetPackage) {
        if (TextUtils.equals(callerPkg, getPackageName())) {
            return true;
        }

        int targetUid = -1;
        try {
            targetUid = getPackageManager().getApplicationInfo(targetPackage,
                    ApplicationInfoFlags.of(/* flags= */ 0)).uid;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Not able to get targetUid: " + e);
            return false;
        }

        // When activityInfo.exported is false, Activity still can be launched if applications have
        // the same user ID.
        if (UserHandle.isSameApp(callerUid, targetUid)) {
            return true;
        }

        // When activityInfo.exported is false, Activity still can be launched if calling app has
        // root or system privilege.
        int callingAppId = UserHandle.getAppId(callerUid);
        if (callingAppId == Process.ROOT_UID || callingAppId == Process.SYSTEM_UID) {
            return true;
        }

        return false;
    }
}
