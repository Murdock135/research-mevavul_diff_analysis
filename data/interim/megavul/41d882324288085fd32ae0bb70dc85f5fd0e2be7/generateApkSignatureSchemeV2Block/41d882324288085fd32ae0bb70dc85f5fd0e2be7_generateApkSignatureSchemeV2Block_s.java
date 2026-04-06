class generateApkSignatureSchemeV2Block {
private static Pair<byte[], Integer> generateApkSignatureSchemeV2Block(
            List<SignerConfig> signerConfigs,
            Map<ContentDigestAlgorithm, byte[]> contentDigests,
            boolean v3SigningEnabled,
            List<byte[]> preservedV2SignerBlocks)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // FORMAT:
        // * length-prefixed sequence of length-prefixed signer blocks.

        List<byte[]> signerBlocks = new ArrayList<>(signerConfigs.size());
        if (preservedV2SignerBlocks != null && preservedV2SignerBlocks.size() > 0) {
            signerBlocks.addAll(preservedV2SignerBlocks);
        }
        int signerNumber = 0;
        for (SignerConfig signerConfig : signerConfigs) {
            signerNumber++;
            byte[] signerBlock;
            try {
                signerBlock = generateSignerBlock(signerConfig, contentDigests, v3SigningEnabled);
            } catch (InvalidKeyException e) {
                throw new InvalidKeyException("Signer #" + signerNumber + " failed", e);
            } catch (SignatureException e) {
                throw new SignatureException("Signer #" + signerNumber + " failed", e);
            }
            signerBlocks.add(signerBlock);
        }

        return Pair.of(
                encodeAsSequenceOfLengthPrefixedElements(
                        new byte[][] {
                            encodeAsSequenceOfLengthPrefixedElements(signerBlocks),
                        }),
                V2SchemeConstants.APK_SIGNATURE_SCHEME_V2_BLOCK_ID);
    }
}
