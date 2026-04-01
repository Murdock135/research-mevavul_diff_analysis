class handleCorsPreflightRequest {
public Map<String, String> handleCorsPreflightRequest(String pOrigin, String pRequestHeaders) {
        Map<String,String> ret = new HashMap<String, String>();
        if (pOrigin != null && backendManager.isCorsAccessAllowed(pOrigin)) {
            // CORS is allowed, we set exactly the origin in the header, so there are no problems with authentication
            ret.put("Access-Control-Allow-Origin","null".equals(pOrigin) ? "*" : pOrigin);
            if (pRequestHeaders != null) {
                ret.put("Access-Control-Allow-Headers",pRequestHeaders);
            }
            // Fix for CORS with authentication (#104)
            ret.put("Access-Control-Allow-Credentials","true");
            // Allow for one year. Changes in access.xml are reflected directly in the  cors request itself
            ret.put("Access-Control-Allow-Max-Age","" + 3600 * 24 * 365);
        }
        return ret;
    }
}
