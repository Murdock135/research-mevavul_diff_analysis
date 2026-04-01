class broadcastCertificationEvent {
public void broadcastCertificationEvent(String iface, int networkId, String ssid,
            int depth, CertificateEventInfo certificateEventInfo) {
        sendMessage(iface, TOFU_CERTIFICATE_EVENT, networkId, depth, certificateEventInfo);
    }
}
