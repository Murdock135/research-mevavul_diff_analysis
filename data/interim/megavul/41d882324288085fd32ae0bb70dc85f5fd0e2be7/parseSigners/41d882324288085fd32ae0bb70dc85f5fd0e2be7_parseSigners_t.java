class parseSigners {
public static void parseSigners(
            ByteBuffer apkSignatureSchemeV2Block,
            Set<ContentDigestAlgorithm> contentDigestsToVerify,
            Map<Integer, String> supportedApkSigSchemeNames,
            Set<Integer> foundApkSigSchemeIds,
            int minSdkVersion,
            int maxSdkVersion,
            ApkSigningBlockUtils.Result result) throws NoSuchAlgorithmException {
        ByteBuffer signers;
        try {
            signers = ApkSigningBlockUtils.getLengthPrefixedSlice(apkSignatureSchemeV2Block);
        } catch (ApkFormatException e) {
            result.addError(Issue.V2_SIG_MALFORMED_SIGNERS);
            return;
        }
        if (!signers.hasRemaining()) {
            result.addError(Issue.V2_SIG_NO_SIGNERS);
            return;
        }

        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException("Failed to obtain X.509 CertificateFactory", e);
        }
        int signerCount = 0;
        while (signers.hasRemaining()) {
            int signerIndex = signerCount;
            signerCount++;
            ApkSigningBlockUtils.Result.SignerInfo signerInfo =
                    new ApkSigningBlockUtils.Result.SignerInfo();
            signerInfo.index = signerIndex;
            result.signers.add(signerInfo);
            try {
                ByteBuffer signer = ApkSigningBlockUtils.getLengthPrefixedSlice(signers);
                parseSigner(
                        signer,
                        certFactory,
                        signerInfo,
                        contentDigestsToVerify,
                        supportedApkSigSchemeNames,
                        foundApkSigSchemeIds,
                        minSdkVersion,
                        maxSdkVersion);
            } catch (ApkFormatException | BufferUnderflowException e) {
                signerInfo.addError(Issue.V2_SIG_MALFORMED_SIGNER);
                return;
            }
        }
        if (signerCount > MAX_APK_SIGNERS) {
            result.addError(Issue.V2_SIG_MAX_SIGNATURES_EXCEEDED, MAX_APK_SIGNERS, signerCount);
        }
    }
}
