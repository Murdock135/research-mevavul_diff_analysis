class getTableName {
private String getTableName(String str) {
        String[] arr = str.split("\\s+(?i)where\\s+");
        return arr[0];
    }
}
