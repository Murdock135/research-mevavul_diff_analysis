class doVerifyPassword {
private VerifyCredentialResponse doVerifyPassword(String password, boolean hasChallenge,
            long challenge, int userId) throws RemoteException {
       checkPasswordReadPermission(userId);
       if (TextUtils.isEmpty(password)) {
           throw new IllegalArgumentException("Password can't be null or empty");
       }
       CredentialHash storedHash = mStorage.readPasswordHash(userId);
       return doVerifyPassword(password, storedHash, hasChallenge, challenge, userId);
    }
}
