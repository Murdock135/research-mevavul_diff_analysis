class guestUserRegistration {
private boolean guestUserRegistration(AbstractRegistrationPage registrationPage)
    {
        registrationPage.clickRegister();

        return ((RegistrationPage) registrationPage).getRegistrationSuccessMessage().isPresent();
    }
}
