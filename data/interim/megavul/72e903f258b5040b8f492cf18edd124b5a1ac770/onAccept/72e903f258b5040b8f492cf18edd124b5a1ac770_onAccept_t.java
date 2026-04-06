class onAccept {
@Override
                public void onAccept(String ssid, int networkId) {
                    log("Accept Root CA cert for " + ssid);
                    sendMessage(CMD_ACCEPT_EAP_SERVER_CERTIFICATE, networkId);
                }
}
