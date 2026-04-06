class isCallingAppPermitted {
@VisibleForTesting
    boolean isCallingAppPermitted(String permission, int callerUid) {
        return TextUtils.isEmpty(permission)
                || checkPermission(permission, /* pid= */ -1, callerUid)
                        == PackageManager.PERMISSION_GRANTED;
    }
}
