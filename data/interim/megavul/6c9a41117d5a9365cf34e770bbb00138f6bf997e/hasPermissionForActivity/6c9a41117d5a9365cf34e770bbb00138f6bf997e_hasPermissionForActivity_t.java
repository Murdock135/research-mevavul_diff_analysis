class hasPermissionForActivity {
public boolean hasPermissionForActivity(Intent intent, String srcPackage) {
        // b/270152142
        if (Intent.ACTION_CHOOSER.equals(intent.getAction())) {
            // Chooser shortcuts is not a valid target
            return false;
        }
        ResolveInfo target = mPm.resolveActivity(intent, 0);
        if (target == null) {
            // Not a valid target
            return false;
        }
        if (TextUtils.isEmpty(target.activityInfo.permission)) {
            // No permission is needed
            return true;
        }
        if (TextUtils.isEmpty(srcPackage)) {
            // The activity requires some permission but there is no source.
            return false;
        }

        // Source does not have sufficient permissions.
        if(mPm.checkPermission(target.activityInfo.permission, srcPackage) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        // On M and above also check AppOpsManager for compatibility mode permissions.
        if (TextUtils.isEmpty(AppOpsManager.permissionToOp(target.activityInfo.permission))) {
            // There is no app-op for this permission, which could have been disabled.
            return true;
        }

        // There is no direct way to check if the app-op is allowed for a particular app. Since
        // app-op is only enabled for apps running in compatibility mode, simply block such apps.

        try {
            return mPm.getApplicationInfo(srcPackage, 0).targetSdkVersion >= Build.VERSION_CODES.M;
        } catch (NameNotFoundException e) { }

        return false;
    }
}
