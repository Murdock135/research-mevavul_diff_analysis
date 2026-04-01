class login {
public String login() {
        previewField(this);
        if (AdminTokenThreadLocal.getUser() != null) {
            redirect(Constants.ADMIN_INDEX);
            return null;
        }
        return LOGOUT_URI;
    }
}
