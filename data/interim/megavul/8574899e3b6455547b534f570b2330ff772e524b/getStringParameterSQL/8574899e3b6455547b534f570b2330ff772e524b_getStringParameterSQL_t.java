class getStringParameterSQL {
@Override
    public String getStringParameterSQL(String param) {
        // DASHBUILDE-113: SQL Injection on data set lookup filters
        String escapedParam = param.replaceAll("'", "''");
        return "'" + escapedParam + "'";
    }
}
