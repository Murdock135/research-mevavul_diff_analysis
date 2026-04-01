class findEntry {
private List<Map<String, Object>> findEntry(String sql, Object... paras) {
        List<Log> logList = find(sql, paras);
        List<Map<String, Object>> convertList = new ArrayList<>();
        for (Log log : logList) {
            convertList.add(log.getAttrs());
        }
        return convertList;
    }
}
