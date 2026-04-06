class doVerifyPattern {
private VerifyCredentialResponse doVerifyPattern(String pattern, boolean hasChallenge,
            long challenge, int userId) throws RemoteException {
       checkPasswordReadPermission(userId);
       CredentialHash storedHash = mStorage.readPatternHash(userId);
       return doVerifyPattern(pattern, storedHash, hasChallenge, challenge, userId);
    }
}
