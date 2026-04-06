class onAccept {
@Override
                public void onAccept(String ssid) {
                    log("Accept Root CA cert for " + ssid);
                    sendMessage(CMD_ACCEPT_EAP_SERVER_CERTIFICATE, ssid);
                }
}
