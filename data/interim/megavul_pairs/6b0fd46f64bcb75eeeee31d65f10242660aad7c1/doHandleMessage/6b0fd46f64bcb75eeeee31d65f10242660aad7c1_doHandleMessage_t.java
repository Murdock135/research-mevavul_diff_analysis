class doHandleMessage {
protected void doHandleMessage(Buffer buffer) throws Exception {
        int cmd = buffer.getUByte();
        if (log.isDebugEnabled()) {
            log.debug("doHandleMessage({}) process #{} {}", this, seqi - 1,
                    SshConstants.getCommandMessageName(cmd));
        }

        switch (cmd) {
            case SshConstants.SSH_MSG_DISCONNECT:
                handleDisconnect(buffer);
                break;
            case SshConstants.SSH_MSG_IGNORE:
                failStrictKex(cmd);
                handleIgnore(buffer);
                break;
            case SshConstants.SSH_MSG_UNIMPLEMENTED:
                failStrictKex(cmd);
                handleUnimplemented(buffer);
                break;
            case SshConstants.SSH_MSG_DEBUG:
                // Fail after handling -- by default a message will be logged, which might be helpful.
                handleDebug(buffer);
                failStrictKex(cmd);
                break;
            case SshConstants.SSH_MSG_SERVICE_REQUEST:
                failStrictKex(cmd);
                handleServiceRequest(buffer);
                break;
            case SshConstants.SSH_MSG_SERVICE_ACCEPT:
                failStrictKex(cmd);
                handleServiceAccept(buffer);
                break;
            case SshConstants.SSH_MSG_KEXINIT:
                handleKexInit(buffer);
                break;
            case SshConstants.SSH_MSG_NEWKEYS:
                handleNewKeys(cmd, buffer);
                break;
            case KexExtensions.SSH_MSG_EXT_INFO:
                failStrictKex(cmd);
                handleKexExtension(cmd, buffer);
                break;
            case KexExtensions.SSH_MSG_NEWCOMPRESS:
                failStrictKex(cmd);
                handleNewCompression(cmd, buffer);
                break;
            default:
                if ((cmd >= SshConstants.SSH_MSG_KEX_FIRST) && (cmd <= SshConstants.SSH_MSG_KEX_LAST)) {
                    if (firstKexPacketFollows != null) {
                        try {
                            if (!handleFirstKexPacketFollows(cmd, buffer, firstKexPacketFollows)) {
                                break;
                            }
                        } finally {
                            firstKexPacketFollows = null; // avoid re-checking
                        }
                    }

                    handleKexMessage(cmd, buffer);
                } else {
                    failStrictKex(cmd);
                    if (currentService.process(cmd, buffer)) {
                        resetIdleTimeout();
                    } else {
                        /*
                         * According to https://tools.ietf.org/html/rfc4253#section-11.4
                         *
                         * An implementation MUST respond to all unrecognized messages with an SSH_MSG_UNIMPLEMENTED
                         * message in the order in which the messages were received.
                         */
                        if (log.isDebugEnabled()) {
                            log.debug("process({}) Unsupported command: {}", this, SshConstants.getCommandMessageName(cmd));
                        }
                        notImplemented(cmd, buffer);
                    }
                }
                break;
        }
        checkRekey();
    }
}
