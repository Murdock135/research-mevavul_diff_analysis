class login {
public String login() {
        AdminInterceptor.previewField(getRequest());
        if (AdminTokenThreadLocal.getUser() != null) {
            redirect(Constants.ADMIN_INDEX);
            return null;
        }
        return LOGOUT_URI;
    }
}
