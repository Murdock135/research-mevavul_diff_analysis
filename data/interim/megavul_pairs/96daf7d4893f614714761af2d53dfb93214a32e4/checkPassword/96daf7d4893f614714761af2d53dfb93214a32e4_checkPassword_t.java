class checkPassword {
public boolean checkPassword(String password, int userId) throws RequestThrottledException {
        throwIfCalledOnMainThread();
        try {
            VerifyCredentialResponse response =
                    getLockSettings().checkPassword(password, userId);
            if (response.getResponseCode() == VerifyCredentialResponse.RESPONSE_OK) {
                return true;
            } else if (response.getResponseCode() == VerifyCredentialResponse.RESPONSE_RETRY) {
                throw new RequestThrottledException(response.getTimeout());
            } else {
                return false;
            }
        } catch (RemoteException re) {
            return false;
        }
    }
}
