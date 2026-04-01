class onSaveStates {
synchronized void onSaveStates(Bundle outStates) {
        try {
            outStates.putSerializable(DATA_KEY, mBundle);
            outStates.putString(KeyChain.EXTRA_NAME, mName);
            outStates.putInt(Credentials.EXTRA_INSTALL_AS_UID, mUid);
            if (mUserKey != null) {
                outStates.putByteArray(Credentials.USER_PRIVATE_KEY,
                        mUserKey.getEncoded());
            }
            ArrayList<byte[]> certs = new ArrayList<byte[]>(mCaCerts.size() + 1);
            if (mUserCert != null) {
                certs.add(mUserCert.getEncoded());
            }
            for (X509Certificate cert : mCaCerts) {
                certs.add(cert.getEncoded());
            }
            outStates.putByteArray(CERTS_KEY, Util.toBytes(certs));
        } catch (CertificateEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
