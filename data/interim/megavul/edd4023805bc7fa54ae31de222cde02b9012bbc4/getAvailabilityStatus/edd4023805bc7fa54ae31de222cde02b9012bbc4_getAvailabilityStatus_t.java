class getAvailabilityStatus {
@Override
    public int getAvailabilityStatus() {
        return mContext.getResources().getBoolean(R.bool.config_show_location_scanning)
                ? (mUserManager.hasUserRestriction(UserManager.DISALLOW_CONFIG_LOCATION)
                       ? DISABLED_DEPENDENT_SETTING
                       : AVAILABLE)
                : UNSUPPORTED_ON_DEVICE;
    }
}
