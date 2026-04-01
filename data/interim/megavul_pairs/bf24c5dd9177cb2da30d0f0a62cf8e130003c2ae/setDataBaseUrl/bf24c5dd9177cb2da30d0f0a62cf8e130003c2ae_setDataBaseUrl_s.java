class setDataBaseUrl {
@Override
    public void setDataBaseUrl(Properties dbconfig, String host, String port, String database, String timeZone)
            throws IOException, URISyntaxException {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://");
        sb.append(host);
        sb.append(":");
        if (CommonUtils.empty(port)) {
            sb.append(getDefaultPort());
        } else {
            sb.append(port);
        }
        sb.append("/");
        sb.append(database);
        sb.append("?characterEncoding=UTF-8&useSSL=false");
        if (CommonUtils.notEmpty(timeZone)) {
            try {
                sb.append("&serverTimezone=GMT");
                sb.append(URLEncoder.encode(timeZone, Constants.DEFAULT_CHARSET_NAME));
            } catch (UnsupportedEncodingException e) {
            }
        }
        dbconfig.setProperty("jdbc.url", sb.toString());
        dbconfig.setProperty("jdbc.driverClassName", "com.mysql.cj.jdbc.Driver");
    }
}
