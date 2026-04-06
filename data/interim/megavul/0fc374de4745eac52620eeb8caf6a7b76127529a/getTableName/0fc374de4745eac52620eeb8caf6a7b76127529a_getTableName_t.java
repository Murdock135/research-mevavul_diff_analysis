class getTableName {
private String getTableName(String str) {
        String[] arr = str.split("\\s+(?i)where\\s+");
        // sys_user , (sys_user), sys_user%20, %60sys_user%60  issues/4393
        String reg = "\\s+|\\(|\\)|`";
        return arr[0].replaceAll(reg, "");
    }
}
