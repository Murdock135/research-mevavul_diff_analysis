class verifyCredentialsAndGetAccount_1 {
private Either<Exception, AccountBO> verifyCredentialsAndGetAccount(final String username) {
        final Optional<CredentialsBO> credentials = credentialsService.getByUsernameUnsafe(username);

        if (credentials.isPresent()) {
            return getAccountById(credentials.get().getAccountId());
        } else {
            return Either.left(new ServiceAuthorizationException(ErrorCode.CREDENTIALS_DOES_NOT_EXIST,
                    "Identifier " + username + " does not exist"));
        }
    }
}
