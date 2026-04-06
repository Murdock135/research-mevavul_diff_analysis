class guestUserRegistration {
private boolean guestUserRegistration(TestUtils testUtils, AbstractRegistrationPage registrationPage)
    {
        registrationPage.clickRegister();

        List<WebElement> infos = testUtils.getDriver().findElements(By.className("infomessage"));
        for (WebElement info : infos) {
            if (info.getText().contains("Registration successful.")) {
                return true;
            }
        }
        return false;
    }
}
