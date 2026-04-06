class sign {
public static List<Pair<String, byte[]>> sign(
            List<SignerConfig> signerConfigs,
            DigestAlgorithm jarEntryDigestAlgorithm,
            Map<String, byte[]> jarEntryDigests,
            List<Integer> apkSigningSchemeIds,
            byte[] sourceManifestBytes,
            String createdBy)
                    throws NoSuchAlgorithmException, ApkFormatException, InvalidKeyException,
                            CertificateException, SignatureException {
        if (signerConfigs.isEmpty()) {
            throw new IllegalArgumentException("At least one signer config must be provided");
        }
        if (signerConfigs.size() > MAX_APK_SIGNERS) {
            throw new IllegalArgumentException(
                    "APK Signature Scheme v1 only supports a maximum of " + MAX_APK_SIGNERS + ", "
                            + signerConfigs.size() + " provided");
        }
        OutputManifestFile manifest =
                generateManifestFile(
                        jarEntryDigestAlgorithm, jarEntryDigests, sourceManifestBytes);

        return signManifest(
                signerConfigs, jarEntryDigestAlgorithm, apkSigningSchemeIds, createdBy, manifest);
    }
}
