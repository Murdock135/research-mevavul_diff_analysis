class initDefaultGuestRestrictions {
private void initDefaultGuestRestrictions() {
        if (mGuestRestrictions.isEmpty()) {
            mGuestRestrictions.putBoolean(UserManager.DISALLOW_OUTGOING_CALLS, true);
            mGuestRestrictions.putBoolean(UserManager.DISALLOW_SMS, true);
            mGuestRestrictions.putBoolean(UserManager.DISALLOW_CONFIG_WIFI, true);
        }
    }
}
