class isGlobalOrSecureSettingRestrictedForUser {
private boolean isGlobalOrSecureSettingRestrictedForUser(String setting, int userId,
            String value, int callingUid) {
        String restriction;
        switch (setting) {
            case Settings.Secure.LOCATION_MODE:
                // Note LOCATION_MODE will be converted into LOCATION_PROVIDERS_ALLOWED
                // in android.provider.Settings.Secure.putStringForUser(), so we shouldn't come
                // here normally, but we still protect it here from a direct provider write.
                if (String.valueOf(Settings.Secure.LOCATION_MODE_OFF).equals(value)) return false;
                restriction = UserManager.DISALLOW_SHARE_LOCATION;
                break;

            case Settings.Secure.LOCATION_PROVIDERS_ALLOWED:
                // See SettingsProvider.updateLocationProvidersAllowedLocked.  "-" is to disable
                // a provider, which should be allowed even if the user restriction is set.
                if (value != null && value.startsWith("-")) return false;
                restriction = UserManager.DISALLOW_SHARE_LOCATION;
                break;

            case Settings.Secure.INSTALL_NON_MARKET_APPS:
                if ("0".equals(value)) return false;
                restriction = UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES;
                break;

            case Settings.Global.ADB_ENABLED:
                if ("0".equals(value)) return false;
                restriction = UserManager.DISALLOW_DEBUGGING_FEATURES;
                break;

            case Settings.Global.PACKAGE_VERIFIER_ENABLE:
            case Settings.Global.PACKAGE_VERIFIER_INCLUDE_ADB:
                if ("1".equals(value)) return false;
                restriction = UserManager.ENSURE_VERIFY_APPS;
                break;

            case Settings.Global.PREFERRED_NETWORK_MODE:
                restriction = UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS;
                break;

            case Settings.Secure.ALWAYS_ON_VPN_APP:
            case Settings.Secure.ALWAYS_ON_VPN_LOCKDOWN:
                // Whitelist system uid (ConnectivityService) and root uid to change always-on vpn
                if (callingUid == Process.SYSTEM_UID || callingUid == Process.ROOT_UID) {
                    return false;
                }
                restriction = UserManager.DISALLOW_CONFIG_VPN;
                break;

            default:
                if (setting != null && setting.startsWith(Settings.Global.DATA_ROAMING)) {
                    if ("0".equals(value)) return false;
                    restriction = UserManager.DISALLOW_DATA_ROAMING;
                    break;
                }
                return false;
        }

        return mUserManager.hasUserRestriction(restriction, UserHandle.of(userId));
    }
}
