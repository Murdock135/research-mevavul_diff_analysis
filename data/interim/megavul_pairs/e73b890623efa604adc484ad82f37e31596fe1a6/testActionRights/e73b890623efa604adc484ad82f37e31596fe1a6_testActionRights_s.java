class testActionRights {
@Order(3)
    @Test
    void testActionRights(TestUtils testUtils, TestReference testReference)
    {
        // set some rights before the test
        DocumentReference xwikiPreferences = new DocumentReference("xwiki", "XWiki", "XWikiPreferences");

        testUtils.loginAsSuperAdmin();
        String anotherUserName = "someOtherUser";
        testUtils.createPage(xwikiPreferences, "");
        testUtils.addObject(xwikiPreferences, "XWiki.XWikiGlobalRights",
            "levels", "edit,script",
            "allow", "1",
            "users", String.format("XWiki.%s,XWiki.%s", USERNAME, anotherUserName));

        testUtils.login(USERNAME, PASSWORD);
        appWithinMinutesHomePage = AppWithinMinutesHomePage.gotoPage();
        try {
            // The application author should be able to edit and delete the application.
            ApplicationsLiveTableElement appsLiveTable = appWithinMinutesHomePage.getAppsLiveTable();
            appsLiveTable.filterApplicationName(appName);
            assertTrue(appsLiveTable.canEditApplication(appName));
            assertTrue(appsLiveTable.canDeleteApplication(appName));

            // Logout. Guests shouldn't be able to edit nor delete the application.
            appWithinMinutesHomePage.logout();
            testUtils.recacheSecretToken();
            appWithinMinutesHomePage = new AppWithinMinutesHomePage();
            appsLiveTable = appWithinMinutesHomePage.getAppsLiveTable();
            appsLiveTable.filterApplicationName(appName);
            assertFalse(appsLiveTable.canEditApplication(appName));
            assertFalse(appsLiveTable.canDeleteApplication(appName));

            // Login with a different user. The new user shouldn't be able to delete the application.
            testUtils.createUserAndLogin(anotherUserName, "somePassword");
            appsLiveTable = AppWithinMinutesHomePage.gotoPage().getAppsLiveTable();
            appsLiveTable.filterApplicationName(appName);
            assertTrue(appsLiveTable.canEditApplication(appName));
            assertFalse(appsLiveTable.canDeleteApplication(appName));
        } finally {
            // We don't want to keep the rights
            testUtils.deletePage(xwikiPreferences);
        }
    }
}
