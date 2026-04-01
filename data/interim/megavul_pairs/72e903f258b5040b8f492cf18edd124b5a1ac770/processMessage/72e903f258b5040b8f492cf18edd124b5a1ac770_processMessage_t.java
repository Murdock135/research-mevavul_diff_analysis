class processMessage {
@Override
        public boolean processMessage(Message message) {
            boolean handleStatus = HANDLED;

            switch (message.what) {
                case CMD_RECONNECT:
                case CMD_REASSOCIATE: {
                    if (mWifiP2pConnection.shouldTemporarilyDisconnectWifi()) {
                        // Drop a third party reconnect/reassociate if STA is
                        // temporarily disconnected for p2p
                        break;
                    } else {
                        // ConnectableState handles it
                        handleStatus = NOT_HANDLED;
                    }
                    break;
                }
                default: {
                    handleStatus = NOT_HANDLED;
                    break;
                }
            }

            if (handleStatus == HANDLED) {
                logStateAndMessage(message, this);
            }
            return handleStatus;
        }
}
