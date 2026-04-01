class isCallingAppPermitted {
@VisibleForTesting
    boolean isCallingAppPermitted(String permission) {
        return TextUtils.isEmpty(permission) || PasswordUtils.isCallingAppPermitted(
                this, getActivityToken(), permission);
    }
}
