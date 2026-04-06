class initDefaultGuestRestrictions {
private void initDefaultGuestRestrictions() {
        if (mGuestRestrictions.isEmpty()) {
            mGuestRestrictions.putBoolean(UserManager.DISALLOW_OUTGOING_CALLS, true);
            mGuestRestrictions.putBoolean(UserManager.DISALLOW_SMS, true);
        }
    }
}
