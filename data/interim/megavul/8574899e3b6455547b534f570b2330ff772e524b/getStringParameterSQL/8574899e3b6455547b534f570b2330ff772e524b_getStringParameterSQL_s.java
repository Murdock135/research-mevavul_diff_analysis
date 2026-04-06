class getStringParameterSQL {
@Override
    public String getStringParameterSQL(String param) {
        return "'" + param + "'";
    }
}
