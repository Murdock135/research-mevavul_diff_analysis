class setAdminPassword {
void setAdminPassword(String password) {
        if (password == null || password.isEmpty()) {
            adminPassword = null;
            return;
        }
        if (password.length() == 128) {
            try {
                adminPassword = StringUtils.convertHexToBytes(password);
                return;
            } catch (Exception ex) {}
        }
        byte[] salt = MathUtils.secureRandomBytes(32);
        byte[] hash = SHA256.getHashWithSalt(password.getBytes(StandardCharsets.UTF_8), salt);
        byte[] total = Arrays.copyOf(salt, 64);
        System.arraycopy(hash, 0, total, 32, 32);
        adminPassword = total;
    }
}
