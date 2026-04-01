class configureProxy {
private void configureProxy() {
        saveProxyConfiguration = saveProxyConfiguration();
        final String host = (String) getInputParameter(PROXY_HOST);
        if (host == null || host.isEmpty()) {
            return;
        }
        LOGGER.info(PROXY_HOST + " " + host);
        final String protocol = (String) getInputParameter(PROXY_PROTOCOL);
        LOGGER.info(PROXY_PROTOCOL + " " + protocol);
        final String port = (String) getInputParameter(PROXY_PORT);
        LOGGER.info(PROXY_PORT + " " + port);

        if (SOCKS.equals(protocol)) {
            System.setProperty("socksProxyHost", host);
            LOGGER.info("Setting environment variable: socksProxyHost=" + host);
            System.setProperty("socksProxyPort", port);
            LOGGER.info("Setting environment variable: socksProxyPort=" + port);
        } else {
            final String hostKey = String.format("%s.proxyHost", protocol.toLowerCase());
            System.setProperty(hostKey, host);
            LOGGER.info("Setting environment variable: " + hostKey + "=" + host);
            final String portKey = String.format("%s.proxyPort", protocol.toLowerCase());
            System.setProperty(portKey, port);
            LOGGER.info("Setting environment variable: " + portKey + "=" + port);
        }

        final String user = (String) getInputParameter(PROXY_USER);
        LOGGER.info(PROXY_USER + " " + user);
        final String password = (String) getInputParameter(PROXY_PASSWORD);
        LOGGER.info(PROXY_PASSWORD + " ********");
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
