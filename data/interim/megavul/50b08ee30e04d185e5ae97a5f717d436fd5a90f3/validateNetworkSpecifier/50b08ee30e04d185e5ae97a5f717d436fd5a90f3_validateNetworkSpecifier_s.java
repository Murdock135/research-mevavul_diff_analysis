class validateNetworkSpecifier {
public static boolean validateNetworkSpecifier(WifiNetworkSpecifier specifier) {
        if (!isValidNetworkSpecifier(specifier)) {
            Log.e(TAG, "validateNetworkSpecifier failed : invalid network specifier");
            return false;
        }
        if (isMatchNoneNetworkSpecifier(specifier)) {
            Log.e(TAG, "validateNetworkSpecifier failed : match-none specifier");
            return false;
        }
        if (isMatchAllNetworkSpecifier(specifier)) {
            Log.e(TAG, "validateNetworkSpecifier failed : match-all specifier");
            return false;
        }
        if (!WifiNetworkSpecifier.validateBand(getBand(specifier))) {
            return false;
        }
        WifiConfiguration config = specifier.wifiConfiguration;
        if (specifier.ssidPatternMatcher.getType() == PatternMatcher.PATTERN_LITERAL) {
            // For literal SSID matches, the value should satisfy SSID requirements.
            // WifiConfiguration.SSID needs quotes around ASCII SSID.
            if (!validateSsid(addEnclosingQuotes(specifier.ssidPatternMatcher.getPath()), true)) {
                return false;
            }
        } else {
            if (config.hiddenSSID) {
                Log.e(TAG, "validateNetworkSpecifier failed : ssid pattern not supported "
                        + "for hidden networks");
                return false;
            }
        }
        if (Objects.equals(specifier.bssidPatternMatcher.second, MacAddress.BROADCAST_ADDRESS)) {
            // For literal BSSID matches, the value should satisfy MAC address requirements.
            if (!validateBssid(specifier.bssidPatternMatcher.first)) {
                return false;
            }
        } else {
            if (!validateBssidPattern(specifier.bssidPatternMatcher)) {
                return false;
            }
        }
        if (!validateBitSets(config)) {
            return false;
        }
        if (!validateKeyMgmt(config.allowedKeyManagement)) {
            return false;
        }
        if (config.isSecurityType(WifiConfiguration.SECURITY_TYPE_PSK)
                && !validatePassword(config.preSharedKey, true, false)) {
            return false;
        }
        if (config.isSecurityType(WifiConfiguration.SECURITY_TYPE_SAE)
                && !validatePassword(config.preSharedKey, true, true)) {
            return false;
        }
        // TBD: Validate some enterprise params as well in the future here.
        return true;
    }
}
