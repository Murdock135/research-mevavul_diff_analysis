class verifyCredentialsAndGetAccount {
private Either<Exception, AccountBO> verifyCredentialsAndGetAccount(final String username, final String password) {
        final Optional<CredentialsBO> credentials = credentialsService.getByUsernameUnsafe(username);

        // TODO replace this with Either mapping
        if (credentials.isPresent()) {
            final Optional<Exception> validationError = checkIdentifier(credentials.get(), username);

            if (validationError.isPresent()) {
                return Either.left(validationError.get());
            }

            if (securePassword.verify(password, credentials.get().getHashedPassword())) {
                return getAccountById(credentials.get().getAccountId());
            } else {
                return Either.left(new ServiceAuthorizationException(ErrorCode.PASSWORDS_DO_NOT_MATCH,
                        "Passwords do not match", EntityType.ACCOUNT, credentials.get().getAccountId()));
            }
        } else {
            return Either.left(new ServiceAuthorizationException(ErrorCode.CREDENTIALS_DOES_NOT_EXIST,
                    "Identifier " + username + " does not exist"));
        }
    }
}
