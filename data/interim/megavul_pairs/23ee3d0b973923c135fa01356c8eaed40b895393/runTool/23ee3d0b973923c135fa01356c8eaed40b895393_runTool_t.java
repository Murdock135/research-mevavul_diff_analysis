class runTool {
@Override
    public void runTool(String... args) throws SQLException {
        boolean tcpStart = false, pgStart = false, webStart = false;
        boolean browserStart = false;
        boolean tcpShutdown = false, tcpShutdownForce = false;
        String tcpPassword = "";
        String tcpShutdownServer = "";
        boolean startDefaultServers = true;
        for (int i = 0; args != null && i < args.length; i++) {
            String arg = args[i];
            if (arg == null) {
            } else if ("-?".equals(arg) || "-help".equals(arg)) {
                showUsage();
                return;
            } else if (arg.startsWith("-web")) {
                if ("-web".equals(arg)) {
                    startDefaultServers = false;
                    webStart = true;
                } else if ("-webAllowOthers".equals(arg)) {
                    // no parameters
                } else if ("-webExternalNames".equals(arg)) {
                    i++;
                } else if ("-webDaemon".equals(arg)) {
                    // no parameters
                } else if ("-webSSL".equals(arg)) {
                    // no parameters
                } else if ("-webPort".equals(arg)) {
                    i++;
                } else if ("-webAdminPassword".equals(arg)) {
                    if (fromCommandLine) {
                        throwUnsupportedOption(arg);
                    }
                    i++;
                } else {
                    showUsageAndThrowUnsupportedOption(arg);
                }
            } else if ("-browser".equals(arg)) {
                startDefaultServers = false;
                browserStart = true;
            } else if (arg.startsWith("-tcp")) {
                if ("-tcp".equals(arg)) {
                    startDefaultServers = false;
                    tcpStart = true;
                } else if ("-tcpAllowOthers".equals(arg)) {
                    // no parameters
                } else if ("-tcpDaemon".equals(arg)) {
                    // no parameters
                } else if ("-tcpSSL".equals(arg)) {
                    // no parameters
                } else if ("-tcpPort".equals(arg)) {
                    i++;
                } else if ("-tcpPassword".equals(arg)) {
                    tcpPassword = args[++i];
                } else if ("-tcpShutdown".equals(arg)) {
                    startDefaultServers = false;
                    tcpShutdown = true;
                    tcpShutdownServer = args[++i];
                } else if ("-tcpShutdownForce".equals(arg)) {
                    tcpShutdownForce = true;
                } else {
                    showUsageAndThrowUnsupportedOption(arg);
                }
            } else if (arg.startsWith("-pg")) {
                if ("-pg".equals(arg)) {
                    startDefaultServers = false;
                    pgStart = true;
                } else if ("-pgAllowOthers".equals(arg)) {
                    // no parameters
                } else if ("-pgDaemon".equals(arg)) {
                    // no parameters
                } else if ("-pgPort".equals(arg)) {
                    i++;
                } else {
                    showUsageAndThrowUnsupportedOption(arg);
                }
            } else if ("-properties".equals(arg)) {
                i++;
            } else if ("-trace".equals(arg)) {
                // no parameters
            } else if ("-ifExists".equals(arg)) {
                // no parameters
            } else if ("-ifNotExists".equals(arg)) {
                // no parameters
            } else if ("-baseDir".equals(arg)) {
                i++;
            } else if ("-key".equals(arg)) {
                i += 2;
            } else {
                showUsageAndThrowUnsupportedOption(arg);
            }
        }
        verifyArgs(args);
        if (startDefaultServers) {
            tcpStart = true;
            pgStart = true;
            webStart = true;
            browserStart = true;
        }
        // TODO server: maybe use one single properties file?
        if (tcpShutdown) {
            out.println("Shutting down TCP Server at " + tcpShutdownServer);
            shutdownTcpServer(tcpShutdownServer, tcpPassword,
                    tcpShutdownForce, false);
        }
        try {
            if (tcpStart) {
                tcp = createTcpServer(args);
                tcp.start();
                out.println(tcp.getStatus());
                tcp.setShutdownHandler(this);
            }
            if (pgStart) {
                pg = createPgServer(args);
                pg.start();
                out.println(pg.getStatus());
            }
            if (webStart) {
                web = createWebServer(args);
                web.setShutdownHandler(this);
                SQLException result = null;
                try {
                    web.start();
                } catch (Exception e) {
                    result = DbException.toSQLException(e);
                }
                out.println(web.getStatus());
                // start browser in any case (even if the server is already
                // running) because some people don't look at the output, but
                // are wondering why nothing happens
                if (browserStart) {
                    try {
                        openBrowser(web.getURL());
                    } catch (Exception e) {
                        out.println(e.getMessage());
                    }
                }
                if (result != null) {
                    throw result;
                }
            } else if (browserStart) {
                out.println("The browser can only start if a web server is started (-web)");
            }
        } catch (SQLException e) {
            stopAll();
            throw e;
        }
    }
}
