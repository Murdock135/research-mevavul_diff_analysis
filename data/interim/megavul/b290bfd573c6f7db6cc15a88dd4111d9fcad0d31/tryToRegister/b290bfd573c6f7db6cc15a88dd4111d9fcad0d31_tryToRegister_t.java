class tryToRegister {
private boolean tryToRegister(TestUtils testUtils, AbstractRegistrationPage registrationPage, boolean isModal)
    {
        if (isModal) {
            return administrationModalUserCreation(testUtils, registrationPage);
        } else {
            return guestUserRegistration(registrationPage);
        }
    }
}
