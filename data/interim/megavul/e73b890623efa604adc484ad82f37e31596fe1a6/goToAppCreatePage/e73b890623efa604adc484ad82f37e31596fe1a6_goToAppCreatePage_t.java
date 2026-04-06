class goToAppCreatePage {
private ApplicationCreatePage goToAppCreatePage(TestUtils testUtils, TestReference testReference)
    {
        // Login and go to the App Within Minutes home page.
        testUtils.login(USER_NAME, PASSWORD);
        // Make sure the application location exists so that we can select it with the location picker.
        testUtils.createPage(Arrays.asList(getClass().getSimpleName(), testReference.getLastSpaceReference().getName()),
            "WebHome", null, null);
        AppWithinMinutesHomePage appWithinMinutesHomePage = AppWithinMinutesHomePage.gotoPage();

        // Click the Create Application button.
        return appWithinMinutesHomePage.clickCreateApplication();
    }
}
