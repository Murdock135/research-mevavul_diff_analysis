class setPasswordIfNotBlank {
private void setPasswordIfNotBlank(String password) {
        this.password = StringUtils.stripToNull(password);
        this.secretParamsForPassword = SecretParams.parse(password);
        this.encryptedPassword = StringUtils.stripToNull(encryptedPassword);

        if (this.password == null) {
            return;
        }
        try {
            this.encryptedPassword = this.goCipher.encrypt(password);
        } catch (Exception e) {
            bomb("Password encryption failed. Please verify your cipher key.", e);
        }
        this.password = null;
    }
}
