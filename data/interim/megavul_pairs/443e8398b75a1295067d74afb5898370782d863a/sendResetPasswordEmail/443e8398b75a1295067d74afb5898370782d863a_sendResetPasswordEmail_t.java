class sendResetPasswordEmail {
public void sendResetPasswordEmail(String username, InternetAddress email, URL resetPasswordURL) throws
        ResetPasswordException
    {
        XWikiContext context = this.contextProvider.get();
        String fromAddress = this.mailSenderConfiguration.getFromAddress();
        if (StringUtils.isEmpty(fromAddress)) {
            fromAddress = NO_REPLY + context.getRequest().getServerName();
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(FROM, fromAddress);
        parameters.put(TO, email);
        parameters.put("language", this.contextProvider.get().getLocale());
        parameters.put("type", "Reset Password");
        Map<String, String> velocityVariables = new HashMap<>();
        velocityVariables.put("userName", username);
        velocityVariables.put("passwordResetURL", resetPasswordURL.toExternalForm());
        parameters.put("velocityVariables", velocityVariables);

        String localizedError =
            this.localizationManager.getTranslationPlain("xe.admin.passwordReset.error.emailFailed");

        try {
            MimeMessage message =
                this.mimeMessageFactory.createMessage(
                    this.documentReferenceResolver.resolve(RESET_PASSWORD_MAIL_TEMPLATE_REFERENCE), parameters);
            this.sendMessage(message, localizedError);
        } catch (MessagingException e) {
            throw new ResetPasswordException(localizedError, e);
        }
    }
}
