class validateForCommonR1andR2 {
private boolean validateForCommonR1andR2() {
        // Required: PerProviderSubscription/<X+>/HomeSP
        if (mHomeSp == null || !mHomeSp.validate()) {
            return false;
        }

        // Required: PerProviderSubscription/<X+>/Credential
        if (mCredential == null || !mCredential.validate()) {
            return false;
        }

        // Optional: PerProviderSubscription/<X+>/Policy
        if (mPolicy != null && !mPolicy.validate()) {
            return false;
        }
        // Optional: DecoratedIdentityPrefix
        if (!TextUtils.isEmpty(mDecoratedIdentityPrefix)) {
            if (!mDecoratedIdentityPrefix.endsWith("!")) {
                EventLog.writeEvent(0x534e4554, "246539931", -1,
                    "Invalid decorated identity prefix");
                return false;
            }
            String[] decoratedIdentityPrefixArray = mDecoratedIdentityPrefix.split("!");
            if (decoratedIdentityPrefixArray.length > MAX_NUMBER_OF_ENTRIES) {
                Log.e(TAG, "too many decoratedIdentityPrefix");
                return false;
            }
            for (String prefix : decoratedIdentityPrefixArray) {
                if (prefix.length() > MAX_STRING_LENGTH) {
                    Log.e(TAG, "The decoratedIdentityPrefix is too long: " + prefix);
                    return false;
                }
            }
        }

        if (mAaaServerTrustedNames != null) {
            if (mAaaServerTrustedNames.length > MAX_NUMBER_OF_ENTRIES) {
                Log.e(TAG, "Too many AaaServerTrustedNames");
                return false;
            }
            for (String fqdn : mAaaServerTrustedNames) {
                if (fqdn.getBytes(StandardCharsets.UTF_8).length > MAX_STRING_LENGTH) {
                    Log.e(TAG, "AaaServerTrustedNames is too long");
                    return false;
                }
            }
        }
        if (mSubscriptionType != null) {
            if (mSubscriptionType.getBytes(StandardCharsets.UTF_8).length > MAX_STRING_LENGTH) {
                Log.e(TAG, "SubscriptionType is too long");
                return false;
            }
        }

        if (mTrustRootCertList != null) {
            if (mTrustRootCertList.size() > MAX_NUMBER_OF_ENTRIES) {
                Log.e(TAG, "Too many TrustRootCert");
                return false;
            }
            for (Map.Entry<String, byte[]> entry : mTrustRootCertList.entrySet()) {
                String url = entry.getKey();
                byte[] certFingerprint = entry.getValue();
                if (TextUtils.isEmpty(url)) {
                    Log.e(TAG, "Empty URL");
                    return false;
                }
                if (url.getBytes(StandardCharsets.UTF_8).length > MAX_URL_BYTES) {
                    Log.e(TAG, "URL bytes exceeded the max: "
                            + url.getBytes(StandardCharsets.UTF_8).length);
                    return false;
                }

                if (certFingerprint == null) {
                    Log.e(TAG, "Fingerprint not specified");
                    return false;
                }
                if (certFingerprint.length != CERTIFICATE_SHA256_BYTES) {
                    Log.e(TAG, "Incorrect size of trust root certificate SHA-256 fingerprint: "
                            + certFingerprint.length);
                    return false;
                }
            }
        }

        if (mServiceFriendlyNames != null) {
            if (mServiceFriendlyNames.size() > MAX_NUMBER_OF_ENTRIES) {
                Log.e(TAG, "ServiceFriendlyNames exceed the max!");
                return false;
            }
            for (Map.Entry<String, String> names : mServiceFriendlyNames.entrySet()) {
                if (names.getKey() == null || names.getValue() == null) {
                    Log.e(TAG, "Service friendly name entry should not be null");
                    return false;
                }
                if (names.getKey().length() > MAX_STRING_LENGTH
                        || names.getValue().length() > MAX_STRING_LENGTH) {
                    Log.e(TAG, "Service friendly name is to long");
                    return false;
                }
            }
        }
        return true;
    }
}
