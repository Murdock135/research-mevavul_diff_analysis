class handle {
@Override
    public void handle(Message msg, SSHPacket buf)
            throws SSHException {
        this.msg = msg;

        log.trace("Received packet {}", msg);

        if (msg.geq(50)) { // not a transport layer packet
            service.handle(msg, buf);
        } else if (msg.in(20, 21) || msg.in(30, 49)) { // kex packet
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
