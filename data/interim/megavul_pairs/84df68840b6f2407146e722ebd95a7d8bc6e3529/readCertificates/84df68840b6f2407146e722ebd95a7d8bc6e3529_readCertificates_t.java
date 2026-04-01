class readCertificates {
synchronized boolean readCertificates() {
        if (metaEntries.isEmpty()) {
            return false;
        }

        int signerCount = 0;
        Iterator<String> it = metaEntries.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key.endsWith(".DSA") || key.endsWith(".RSA") || key.endsWith(".EC")) {
                if (++signerCount > MAX_JAR_SIGNERS) {
                    throw new SecurityException(
                            "APK Signature Scheme v1 only supports a maximum of " + MAX_JAR_SIGNERS
                                    + " signers");
                }
                verifyCertificate(key);
                it.remove();
            }
        }
        return true;
    }
}
