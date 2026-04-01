class verifyCredentialsAndGetAccount_1 {
private Either<Exception, AccountBO> verifyCredentialsAndGetAccount(final String username) {
        final Optional<CredentialsBO> credentials = credentialsService.getByUsernameUnsafe(username);

        if (credentials.isPresent()) {
            final Optional<Exception> validationError = checkIdentifier(credentials.get(), username);

            if (validationError.isPresent()) {
                return Either.left(validationError.get());
            }

            return getAccountById(credentials.get().getAccountId());
        } else {
            return Either.left(new ServiceAuthorizationException(ErrorCode.CREDENTIALS_DOES_NOT_EXIST,
                    "Identifier " + username + " does not exist"));
        }
    }
}
