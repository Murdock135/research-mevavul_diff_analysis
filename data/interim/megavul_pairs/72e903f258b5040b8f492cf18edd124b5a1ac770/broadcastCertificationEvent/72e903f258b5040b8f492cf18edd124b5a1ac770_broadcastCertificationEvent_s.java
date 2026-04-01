class broadcastCertificationEvent {
public void broadcastCertificationEvent(String iface, int networkId, String ssid,
            int depth, X509Certificate cert) {
        sendMessage(iface, TOFU_ROOT_CA_CERTIFICATE, networkId, depth, cert);
    }
}
