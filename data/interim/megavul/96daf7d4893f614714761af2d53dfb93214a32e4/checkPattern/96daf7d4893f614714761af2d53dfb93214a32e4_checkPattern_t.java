class checkPattern {
public boolean checkPattern(List<LockPatternView.Cell> pattern, int userId)
            throws RequestThrottledException {
        throwIfCalledOnMainThread();
        try {
            VerifyCredentialResponse response =
                    getLockSettings().checkPattern(patternToString(pattern), userId);

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
