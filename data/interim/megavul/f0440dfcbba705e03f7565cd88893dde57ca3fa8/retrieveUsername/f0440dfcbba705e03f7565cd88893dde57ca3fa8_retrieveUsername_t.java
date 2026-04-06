class retrieveUsername {
@Test
    public void retrieveUsername(TestUtils testUtils) throws Exception
    {
        // We create three users, two of them are sharing the same email
        String user1Login = "realuser1";
        String user1Email = "realuser@host.org";

        String user2Login = "realuser2";
        String user2Email = "realuser@host.org";

        String user3Login = "foo";
        String user3Email = "foo@host.org";

        // We need to login as superadmin to set the user email.
        testUtils.loginAsSuperAdmin();
        testUtils.createUser(user1Login, "realuserpwd", testUtils.getURLToNonExistentPage(), "email", user1Email);
        testUtils.createUser(user2Login, "realuserpwd", testUtils.getURLToNonExistentPage(), "email", user2Email);
        testUtils.createUser(user3Login, "realuserpwd", testUtils.getURLToNonExistentPage(), "email", user3Email);

        testUtils.forceGuestUser();

        // check that when asking to retrieve username with a wrong email we don't get any information
        // if an user exists or not and no email is sent.
        ForgotUsernamePage forgotUsernamePage = ForgotUsernamePage.gotoPage();
        forgotUsernamePage.setEmail("notexistant@xwiki.com");
        ForgotUsernameCompletePage forgotUsernameCompletePage = forgotUsernamePage.clickRetrieveUsername();
        assertTrue(forgotUsernameCompletePage.isForgotUsernameQuerySent());

        // we are waiting 5 sec here just to be sure no mail is sent, maybe we could decrease the timeout value,
        // not sure.
        assertFalse(this.mail.waitForIncomingEmail(1));

        // Bypass the check that prevents to reload the current page
        testUtils.gotoPage(testUtils.getURLToNonExistentPage());

        // test getting email for a forgot username request where the email is set in one account only
        forgotUsernamePage = ForgotUsernamePage.gotoPage();
        forgotUsernamePage.setEmail(user3Email);
        forgotUsernameCompletePage = forgotUsernamePage.clickRetrieveUsername();
        assertTrue(forgotUsernameCompletePage.isForgotUsernameQuerySent());
        assertTrue(this.mail.waitForIncomingEmail(1));
        MimeMessage[] receivedEmails = this.mail.getReceivedMessages();
        assertEquals(1, receivedEmails.length);
        MimeMessage receivedEmail = receivedEmails[0];
        assertTrue(receivedEmail.getSubject().contains("Forgot username on"));
        String receivedMailContent = getMessageContent(receivedEmail).get("textPart");
        assertTrue(receivedMailContent.contains(String.format("XWiki.%s", user3Login)));

        // remove mails for last test
        this.mail.purgeEmailFromAllMailboxes();

        // Bypass the check that prevents to reload the current page
        testUtils.gotoPage(testUtils.getURLToNonExistentPage());

        // test getting email for a forgot username request where the email is set in two accounts
        forgotUsernamePage = ForgotUsernamePage.gotoPage();
        forgotUsernamePage.setEmail(user1Email);
        forgotUsernameCompletePage = forgotUsernamePage.clickRetrieveUsername();
        assertTrue(forgotUsernameCompletePage.isForgotUsernameQuerySent());
        assertTrue(this.mail.waitForIncomingEmail(1));
        receivedEmails = this.mail.getReceivedMessages();
        assertEquals(1, receivedEmails.length);
        receivedEmail = receivedEmails[0];
        assertTrue(receivedEmail.getSubject().contains("Forgot username on"));
        receivedMailContent = getMessageContent(receivedEmail).get("textPart");
        assertTrue(receivedMailContent.contains(String.format("XWiki.%s", user1Login)));
        assertTrue(receivedMailContent.contains(String.format("XWiki.%s", user2Login)));
    }
}
