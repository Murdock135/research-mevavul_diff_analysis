class getExecSqlList {
public static List<Map.Entry<Integer, List<String>>> getExecSqlList(String sqlVersion, String basePath) {
        List<Map.Entry<Integer, List<String>>> sqlList = new ArrayList<>();
        Integer version = 0;
        try {
            version = Integer.valueOf(sqlVersion);
        } catch (Exception e) {
            LOGGER.error("",e);
        }
        for (File f : getSqlFileList(basePath)) {
            try {
                Integer fileVersion = Integer.valueOf(f.getName().replace(".sql", ""));
                if (fileVersion > version) {
                    LOGGER.info("need update sql " + f);
                    Map.Entry<Integer, List<String>> entry = new AbstractMap.SimpleEntry<>(fileVersion, Arrays.asList(IOUtil.getStringInputStream(new FileInputStream(f)).split("\n")));
                    sqlList.add(entry);
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("", e);
            }
        }
        return sqlList;
    }
}
