class handle {
@Override
    public void handle(Message msg, SSHPacket buf)
            throws SSHException {
        this.msg = msg;

        log.trace("Received packet {}", msg);

        if (kexer.isInitialKex()) {
            if (decoder.isSequenceNumberAtMax()) {
                throw new TransportException(DisconnectReason.KEY_EXCHANGE_FAILED,
                    "Sequence number of decoder is about to wrap during initial key exchange");
            }
            if (kexer.isStrictKex() && !isKexerPacket(msg) && msg != Message.DISCONNECT) {
                throw new TransportException(DisconnectReason.KEY_EXCHANGE_FAILED,
                    "Unexpected packet type during initial strict key exchange");
            }
        }

        if (msg.geq(50)) { // not a transport layer packet
            service.handle(msg, buf);
        } else if (isKexerPacket(msg)) {
            kexer.handle(msg, buf);
        } else {
            switch (msg) {
                case DISCONNECT:
                    gotDisconnect(buf);
                    break;
                case IGNORE:
                    log.debug("Received SSH_MSG_IGNORE");
                    break;
                case UNIMPLEMENTED:
                    gotUnimplemented(buf);
                    break;
                case DEBUG:
                    gotDebug(buf);
                    break;
                case SERVICE_ACCEPT:
                    gotServiceAccept();
                    break;
                case EXT_INFO:
                    log.debug("Received SSH_MSG_EXT_INFO");
                    break;
                case USERAUTH_BANNER:
                    log.debug("Received USERAUTH_BANNER");
                    break;
                default:
                    sendUnimplemented();
                    break;
            }
        }
    }
}
