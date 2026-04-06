class setAdminPassword {
void setAdminPassword(String password) {
        if (password == null || password.isEmpty()) {
            adminPassword = null;
            return;
        }
        if (password.length() != 128) {
            throw new IllegalArgumentException(
                    "Use result of org.h2.server.web.WebServer.encodeAdminPassword(String)");
        }
        adminPassword = StringUtils.convertHexToBytes(password);
    }
}
