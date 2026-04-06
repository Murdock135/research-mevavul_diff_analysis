class getRedirectParameters {
private String getRedirectParameters(String parent, String title, String template, ActionOnCreate actionOnCreate)
    {
        if (actionOnCreate == ActionOnCreate.SAVE_AND_EDIT) {
            // We don't need to pass any parameters because the document is saved before the redirect using the
            // parameter values.
            return null;
        }

        String redirectParams = "template=" + Util.encodeURI(template, null);
        if (parent != null) {
            redirectParams += "&parent=" + Util.encodeURI(parent, null);
        }
        if (title != null) {
            redirectParams += "&title=" + Util.encodeURI(title, null);
        }
        // Both the save and the edit action might require a CSRF token
        CSRFToken csrf = Utils.getComponent(CSRFToken.class);
        redirectParams += "&form_token=" + Util.encodeURI(csrf.getToken(), null);

        return redirectParams;
    }
}
