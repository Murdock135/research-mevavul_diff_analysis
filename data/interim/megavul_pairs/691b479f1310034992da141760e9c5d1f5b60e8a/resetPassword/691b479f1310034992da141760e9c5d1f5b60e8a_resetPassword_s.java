class resetPassword {
private void resetPassword(String passwordToSet) {
        if (StringUtils.isBlank(passwordToSet)) {
            encryptedPassword = null;
        }
        setPasswordIfNotBlank(passwordToSet);
    }
}
