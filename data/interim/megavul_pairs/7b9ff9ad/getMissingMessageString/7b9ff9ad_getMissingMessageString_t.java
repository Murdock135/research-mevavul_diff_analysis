class getMissingMessageString {
private String getMissingMessageString(String messageId) {
        String caller = "";
        StackTraceElement callerElement = getCallingMethod();
        if (callerElement != null) {
            caller = " called by " + callerElement;
        }
        if (messageId == null) {
            messageId = "null";
        }
        String message = "*** ERROR: Message with id: [" + messageId +
                "] not found.***" + caller;
        log.error(message);
        boolean exceptionMode = Config.get().getBoolean(
                "java.l10n_missingmessage_exceptions");
        if (exceptionMode) {
            throw new IllegalArgumentException(message);
        }
        return StringEscapeUtils.escapeHtml("**" + messageId + "**");
    }
}
