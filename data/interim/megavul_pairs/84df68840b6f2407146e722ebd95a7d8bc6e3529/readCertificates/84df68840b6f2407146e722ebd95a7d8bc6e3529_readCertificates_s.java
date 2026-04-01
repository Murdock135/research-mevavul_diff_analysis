class readCertificates {
synchronized boolean readCertificates() {
        if (metaEntries.isEmpty()) {
            return false;
        }

        Iterator<String> it = metaEntries.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key.endsWith(".DSA") || key.endsWith(".RSA") || key.endsWith(".EC")) {
                verifyCertificate(key);
                it.remove();
            }
        }
        return true;
    }
}
