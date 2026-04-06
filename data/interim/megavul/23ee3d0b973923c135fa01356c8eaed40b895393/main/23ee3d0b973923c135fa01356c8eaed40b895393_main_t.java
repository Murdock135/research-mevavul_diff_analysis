class main {
public static void main(String... args) throws SQLException {
        Server server = new Server();
        server.fromCommandLine = true;
        server.runTool(args);
    }
}
