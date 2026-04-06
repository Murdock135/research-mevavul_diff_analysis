class hex {
private static String hex(String origName, String candidate) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] sum = md.digest(candidate.getBytes(UTF_8));
        //convert the byte to hex format method 2
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < sum.length; i++) {
            hexString.append(Integer.toHexString(0xFF & sum[i]));
        }
        String extension = "";
        int i = origName.lastIndexOf('.');
        if (i > 0) {
            extension = origName.substring(i);//contains dot
        }
        if (extension.length() < 10 && extension.length() > 1) {
            hexString.append(extension);
        }
        return hexString.toString();
    }
}
