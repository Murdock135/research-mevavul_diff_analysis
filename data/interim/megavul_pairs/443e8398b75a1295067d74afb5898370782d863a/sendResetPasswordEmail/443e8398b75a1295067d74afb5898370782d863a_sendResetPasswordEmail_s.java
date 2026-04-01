class sendResetPasswordEmail {
public void sendResetPasswordEmail(String username, InternetAddress email, URL resetPasswordURL) throws
        ResetPasswordException
    {
        XWikiContext context = this.contextProvider.get();
        String fromAddress = this.mailSenderConfiguration.getFromAddress();
        if (StringUtils.isEmpty(fromAddress)) {
            fromAddress = "no-reply@" + context.getRequest().getServerName();
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", fromAddress);
        parameters.put("to", email);
        parameters.put("language", context.getLocale());
        parameters.put("type", "Reset Password");
        Map<String, String> velocityVariables = new HashMap<>();
        velocityVariables.put("userName", username);
        velocityVariables.put("passwordResetURL", resetPasswordURL.toExternalForm());
        parameters.put("velocityVariables", velocityVariables);

        String localizedError =
            this.localizationManager.getTranslationPlain("xe.admin.passwordReset.error.emailFailed");

        MimeMessage message;
        try {
            message =
                this.mimeMessageFactory.createMessage(
                    this.documentReferenceResolver.resolve(RESET_PASSWORD_MAIL_TEMPLATE_REFERENCE), parameters);
        } catch (MessagingException e) {
            throw new ResetPasswordException(localizedError, e);
        }
        MailListener mailListener = this.mailListenerProvider.get();
        this.mailSender.sendAsynchronously(Collections.singleton(message),
            this.sessionFactory.create(Collections.emptyMap()),
            mailListener);

        MailStatusResult mailStatusResult = mailListener.getMailStatusResult();
        mailStatusResult.waitTillProcessed(30L);
        Iterator<MailStatus> mailErrors = mailStatusResult.getAllErrors();

        if (mailErrors != null && mailErrors.hasNext()) {
            MailStatus lastError = mailErrors.next();
            throw new ResetPasswordException(
                String.format("%s - %s", localizedError, lastError.getErrorDescription()));
        }
    }
}
