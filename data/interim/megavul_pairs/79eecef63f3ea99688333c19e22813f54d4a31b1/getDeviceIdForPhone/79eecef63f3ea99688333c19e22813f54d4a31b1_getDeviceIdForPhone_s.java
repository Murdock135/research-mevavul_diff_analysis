class getDeviceIdForPhone {
public String getDeviceIdForPhone(int phoneId) {
        Phone phone = getPhone(phoneId);
        if (phone != null) {
            return phone.getDeviceId();
        } else {
            Rlog.e(TAG,"getDeviceIdForPhone phone " + phoneId + " is null");
            return null;
        }
    }
}
