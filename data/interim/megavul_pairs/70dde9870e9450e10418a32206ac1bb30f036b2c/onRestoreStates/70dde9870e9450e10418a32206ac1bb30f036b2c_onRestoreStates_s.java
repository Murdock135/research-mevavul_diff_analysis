class onRestoreStates {
void onRestoreStates(Bundle savedStates) {
        mBundle = (HashMap) savedStates.getSerializable(DATA_KEY);
        mName = savedStates.getString(KeyChain.EXTRA_NAME);
        byte[] bytes = savedStates.getByteArray(Credentials.USER_PRIVATE_KEY);
        if (bytes != null) {
            setPrivateKey(bytes);
        }

        ArrayList<byte[]> certs = Util.fromBytes(savedStates.getByteArray(CERTS_KEY));
        for (byte[] cert : certs) {
            parseCert(cert);
        }
    }
}
