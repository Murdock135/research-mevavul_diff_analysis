class setPasswordIfNotBlank {
private void setPasswordIfNotBlank(String password) {
        this.password = StringUtils.stripToNull(password);
        this.secretParamsForPassword = SecretParams.parse(password);
    }
}
