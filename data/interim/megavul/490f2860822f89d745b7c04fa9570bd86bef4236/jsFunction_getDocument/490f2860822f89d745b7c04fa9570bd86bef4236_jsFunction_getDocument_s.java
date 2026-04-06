class jsFunction_getDocument {
public static NativeObject jsFunction_getDocument(Context cx, Scriptable thisObj,
                                                      Object[] args, Function funObj)
            throws ScriptException,
                   APIManagementException {
        if (args == null || args.length != 2 || !isStringArray(args)) {
            handleException("Invalid input parameters expected resource Url and tenantDomain");
        }
        NativeObject data = new NativeObject();

        String username = getUsernameFromObject(thisObj);
        // Set anonymous user if no user is login to the system
        if (username == null) {
            username = APIConstants.END_USER_ANONYMOUS;
        }
        String resource = (String) args[1];
        String tenantDomain = (String) args[0];
        if (tenantDomain == null) {
            tenantDomain = MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
        }
        Map<String, Object> docResourceMap = APIUtil.getDocument(username, resource, tenantDomain);
        if (!docResourceMap.isEmpty()) {
            data.put("Data", data,
                     cx.newObject(thisObj, "Stream", new Object[] { docResourceMap.get("Data") }));
            data.put("contentType", data, docResourceMap.get("contentType"));
            data.put("name", data, docResourceMap.get("name"));
        }
        return data;
    }
}
