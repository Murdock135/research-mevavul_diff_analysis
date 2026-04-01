class mergeFrom {
private void mergeFrom(ApkSigningBlockUtils.Result source) {
            switch (source.signatureSchemeVersion) {
                case ApkSigningBlockUtils.VERSION_APK_SIGNATURE_SCHEME_V2:
                    mVerifiedUsingV2Scheme = source.verified;
                    for (ApkSigningBlockUtils.Result.SignerInfo signer : source.signers) {
                        mV2SchemeSigners.add(new V2SchemeSignerInfo(signer));
                    }
                    break;
                case ApkSigningBlockUtils.VERSION_APK_SIGNATURE_SCHEME_V3:
                    mVerifiedUsingV3Scheme = source.verified;
                    for (ApkSigningBlockUtils.Result.SignerInfo signer : source.signers) {
                        mV3SchemeSigners.add(new V3SchemeSignerInfo(signer));
                    }
                    // Do not overwrite a previously set lineage from a v3.1 signing block.
                    if (mSigningCertificateLineage == null) {
                        mSigningCertificateLineage = source.signingCertificateLineage;
                    }
                    break;
                case ApkSigningBlockUtils.VERSION_APK_SIGNATURE_SCHEME_V31:
                    mVerifiedUsingV31Scheme = source.verified;
                    for (ApkSigningBlockUtils.Result.SignerInfo signer : source.signers) {
                        mV31SchemeSigners.add(new V3SchemeSignerInfo(signer));
                    }
                    mSigningCertificateLineage = source.signingCertificateLineage;
                    break;
                case ApkSigningBlockUtils.VERSION_APK_SIGNATURE_SCHEME_V4:
                    mVerifiedUsingV4Scheme = source.verified;
                    for (ApkSigningBlockUtils.Result.SignerInfo signer : source.signers) {
                        mV4SchemeSigners.add(new V4SchemeSignerInfo(signer));
                    }
                    break;
                case ApkSigningBlockUtils.VERSION_SOURCE_STAMP:
                    mSourceStampVerified = source.verified;
                    if (!source.signers.isEmpty()) {
                        mSourceStampInfo = new SourceStampInfo(source.signers.get(0));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Signing Block Scheme Id");
            }
        }
}
