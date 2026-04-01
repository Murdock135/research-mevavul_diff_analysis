class getDeviceIdForPhone {
public String getDeviceIdForPhone(int phoneId) {
        Phone phone = getPhone(phoneId);
        if (phone != null) {
            phone.getContext().enforceCallingOrSelfPermission(
                    android.Manifest.permission.READ_PHONE_STATE,
                    "Requires READ_PHONE_STATE");
            return phone.getDeviceId();
        } else {
            Rlog.e(TAG,"getDeviceIdForPhone phone " + phoneId + " is null");
            return null;
        }
    }
}
