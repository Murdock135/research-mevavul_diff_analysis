class writeToParcel {
@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mResponseCode);
        if (mResponseCode == RESPONSE_RETRY) {
            dest.writeInt(mTimeout);
        } else if (mResponseCode == RESPONSE_OK) {
            dest.writeInt(mShouldReEnroll ? 1 : 0);
            if (mPayload != null) {
                dest.writeInt(mPayload.length);
                dest.writeByteArray(mPayload);
            }
        }
    }
}
