class doVerifyPassword {
private VerifyCredentialResponse doVerifyPassword(String password, boolean hasChallenge,
            long challenge, int userId) throws RemoteException {
       checkPasswordReadPermission(userId);
       CredentialHash storedHash = mStorage.readPasswordHash(userId);
       return doVerifyPassword(password, storedHash, hasChallenge, challenge, userId);
    }
}
