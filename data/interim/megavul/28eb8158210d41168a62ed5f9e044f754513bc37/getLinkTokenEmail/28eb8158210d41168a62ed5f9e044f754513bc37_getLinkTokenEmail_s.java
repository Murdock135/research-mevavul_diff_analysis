class getLinkTokenEmail {
public static String getLinkTokenEmail(Context context, String bitstreamId
            , int itemID, String reqEmail, String reqName, boolean allfiles)
            throws SQLException
    {
        String base = ConfigurationManager.getProperty("dspace.url");

        String specialLink = (new StringBuffer()).append(base).append(
                base.endsWith("/") ? "" : "/").append(
                "request-item").append("?step=" + RequestItemServlet.ENTER_TOKEN)
                .append("&token=")
                .append(getNewToken(context, Integer.parseInt(bitstreamId), itemID, reqEmail, reqName, allfiles))
                .toString();
        
        return specialLink;
    }
}
