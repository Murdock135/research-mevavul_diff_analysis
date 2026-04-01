class doVerifyPattern {
private VerifyCredentialResponse doVerifyPattern(String pattern, boolean hasChallenge,
            long challenge, int userId) throws RemoteException {
       checkPasswordReadPermission(userId);
       if (TextUtils.isEmpty(pattern)) {
           throw new IllegalArgumentException("Pattern can't be null or empty");
       }
       CredentialHash storedHash = mStorage.readPatternHash(userId);
       return doVerifyPattern(pattern, storedHash, hasChallenge, challenge, userId);
    }
}
