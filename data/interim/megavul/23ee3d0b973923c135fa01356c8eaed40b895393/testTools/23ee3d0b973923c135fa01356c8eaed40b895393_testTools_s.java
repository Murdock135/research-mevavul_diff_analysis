class testTools {
private void testTools() throws Exception {
        if (config.memory || config.cipher != null) {
            return;
        }
        deleteDb(getTestName());
        Connection conn = getConnection(getTestName());
        conn.createStatement().execute(
                "create table test(id int) as select 1");
        conn.close();
        Server server = new Server();
        server.setOut(new PrintStream(new ByteArrayOutputStream()));
        server.runTool("-web", "-webPort", "8182",
                "-properties", "null", "-tcp", "-tcpPort", "9101", "-webAdminPassword", "123");
        try {
            String url = "http://localhost:8182";
            WebClient client;
            String result;
            client = new WebClient();
            result = client.get(url);
            client.readSessionId(result);
            result = client.get(url, "adminLogin.do?password=123");
            result = client.get(url, "tools.jsp");
            FileUtils.delete(getBaseDir() + "/backup.zip");
            result = client.get(url, "tools.do?tool=Backup&args=-dir," +
                    getBaseDir() + ",-db," + getTestName() + ",-file," +
                    getBaseDir() + "/backup.zip");
            deleteDb(getTestName());
            assertTrue(FileUtils.exists(getBaseDir() + "/backup.zip"));
            result = client.get(url,
                    "tools.do?tool=DeleteDbFiles&args=-dir," +
                    getBaseDir() + ",-db," + getTestName());
            String fn = getBaseDir() + "/" + getTestName() + Constants.SUFFIX_MV_FILE;
            assertFalse(FileUtils.exists(fn));
            result = client.get(url, "tools.do?tool=Restore&args=-dir," +
                    getBaseDir() + ",-db," + getTestName() +",-file," + getBaseDir() +
                    "/backup.zip");
            assertTrue(FileUtils.exists(fn));
            FileUtils.delete(getBaseDir() + "/web.h2.sql");
            FileUtils.delete(getBaseDir() + "/backup.zip");
            result = client.get(url, "tools.do?tool=Recover&args=-dir," +
                    getBaseDir() + ",-db," + getTestName());
            assertTrue(FileUtils.exists(getBaseDir() + "/" + getTestName() + ".h2.sql"));
            FileUtils.delete(getBaseDir() + "/web.h2.sql");
            result = client.get(url, "tools.do?tool=RunScript&args=-script," +
                    getBaseDir() + "/" + getTestName() + ".h2.sql,-url," +
                    getURL(getTestName(), true) +
                    ",-user," + getUser() + ",-password," + getPassword());
            FileUtils.delete(getBaseDir() + "/" + getTestName() + ".h2.sql");
            assertTrue(FileUtils.exists(fn));
            deleteDb(getTestName());
        } finally {
            server.shutdown();
        }
    }
}
