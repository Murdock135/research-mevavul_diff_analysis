class validate {
public boolean validate() {
        if (mPolicyUpdate == null) {
            Log.d(TAG, "PolicyUpdate not specified");
            return false;
        }
        if (!mPolicyUpdate.validate()) {
            return false;
        }

        // Validate SSID exclusion list.
        if (mExcludedSsidList != null) {
            if (mExcludedSsidList.length > MAX_EXCLUSION_SSIDS) {
                Log.d(TAG, "SSID exclusion list size exceeded the max: "
                        + mExcludedSsidList.length);
                return false;
            }
            for (String ssid : mExcludedSsidList) {
                if (ssid.getBytes(StandardCharsets.UTF_8).length > MAX_SSID_BYTES) {
                    Log.d(TAG, "Invalid SSID: " + ssid);
                    return false;
                }
            }
        }
        // Validate required protocol to port map.
        if (mRequiredProtoPortMap != null) {
            for (Map.Entry<Integer, String> entry : mRequiredProtoPortMap.entrySet()) {
                String portNumber = entry.getValue();
                if (portNumber.getBytes(StandardCharsets.UTF_8).length > MAX_PORT_STRING_BYTES) {
                    Log.d(TAG, "PortNumber string bytes exceeded the max: " + portNumber);
                    return false;
                }
            }
        }
        // Validate preferred roaming partner list.
        if (mPreferredRoamingPartnerList != null) {
            for (RoamingPartner partner : mPreferredRoamingPartnerList) {
                if (!partner.validate()) {
                    return false;
                }
            }
        }
        return true;
    }
}
