class configureProxy {
private void configureProxy() {
        saveProxyConfiguration = saveProxyConfiguration();
        final String host = (String) getInputParameter(PROXY_HOST);
        if (host == null || host.isEmpty()) {
            return;
        }
        logger.info(PROXY_HOST + " " + host);
        final String protocol = (String) getInputParameter(PROXY_PROTOCOL);
        logger.info(PROXY_PROTOCOL + " " + protocol);
        final String port = (String) getInputParameter(PROXY_PORT);
        logger.info(PROXY_PORT + " " + port);

        if (SOCKS.equals(protocol)) {
            System.setProperty("socksProxyHost", host);
            logger.info("Setting environment variable: socksProxyHost=" + host);
            System.setProperty("socksProxyPort", port);
            logger.info("Setting environment variable: socksProxyPort=" + port);
        } else {
            final String hostKey = String.format("%s.proxyHost", protocol.toLowerCase());
            System.setProperty(hostKey, host);
            logger.info("Setting environment variable: " + hostKey + "=" + host);
            final String portKey = String.format("%s.proxyPort", protocol.toLowerCase());
            System.setProperty(portKey, port);
            logger.info("Setting environment variable: " + portKey + "=" + port);
        }

        final String user = (String) getInputParameter(PROXY_USER);
        logger.info(PROXY_USER + " " + user);
        final String password = (String) getInputParameter(PROXY_PASSWORD);
        logger.info(PROXY_PASSWORD + " ********");
        if (user != null && !user.isEmpty()) {
            Authenticator.setDefault(new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user,
                            password != null ? password.toCharArray() : "".toCharArray());
                }

            });
        }

    }
}
