class validatePassword {
private static boolean validatePassword(String password, boolean isAdd, boolean isSae) {
        if (isAdd) {
            if (password == null) {
                Log.e(TAG, "validatePassword: null string");
                return false;
            }
        } else {
            if (password == null) {
                // This is an update, so the psk can be null if that is not being changed.
                return true;
            } else if (password.equals(PASSWORD_MASK)) {
                // This is an update, so the app might have returned back the masked password, let
                // it thru. WifiConfigManager will handle it.
                return true;
            }
        }
        if (password.isEmpty()) {
            Log.e(TAG, "validatePassword failed: empty string");
            return false;
        }
        if (password.startsWith("\"")) {
            // ASCII PSK string
            byte[] passwordBytes = password.getBytes(StandardCharsets.US_ASCII);
            int targetMinLength;

            if (isSae) {
                targetMinLength = SAE_ASCII_MIN_LEN;
            } else {
                targetMinLength = PSK_ASCII_MIN_LEN;
            }
            if (passwordBytes.length < targetMinLength) {
                Log.e(TAG, "validatePassword failed: ASCII string size too small: "
                        + passwordBytes.length);
                return false;
            }
            if (passwordBytes.length > PSK_SAE_ASCII_MAX_LEN) {
                Log.e(TAG, "validatePassword failed: ASCII string size too large: "
                        + passwordBytes.length);
                return false;
            }
        } else {
            // HEX PSK string
            if (password.length() != PSK_SAE_HEX_LEN) {
                Log.e(TAG, "validatePassword failed: hex string size mismatch: "
                        + password.length());
                return false;
            }
        }
        try {
            NativeUtil.hexOrQuotedStringToBytes(password);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "validatePassword failed: malformed string: " + password);
            return false;
        }
        return true;
    }
}
