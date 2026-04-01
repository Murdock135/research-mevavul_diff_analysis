class getAuthentication {
public Authentication getAuthentication(HttpServletRequest request, byte[] key, String alias) {
        // verify:
        String identity = request.getParameter("openid.identity");
        if (identity==null)
            throw new OpenIdException("Missing 'openid.identity'.");
        if (request.getParameter("openid.invalidate_handle")!=null)
            throw new OpenIdException("Invalidate handle.");
        String sig = request.getParameter("openid.sig");
        if (sig==null)
            throw new OpenIdException("Missing 'openid.sig'.");
        String signed = request.getParameter("openid.signed");
        if (signed==null)
            throw new OpenIdException("Missing 'openid.signed'.");
        if (!returnTo.equals(request.getParameter("openid.return_to")))
            throw new OpenIdException("Bad 'openid.return_to'.");
        // check sig:
        String[] params = signed.split("[\\,]+");
        StringBuilder sb = new StringBuilder(1024);
        for (String param : params) {
            sb.append(param)
              .append(':');
            String value = request.getParameter("openid." + param);
            if (value!=null)
                sb.append(value);
            sb.append('\n');
        }
        String hmac = getHmacSha1(sb.toString(), key);
        if (!sig.equals(hmac))
            throw new OpenIdException("Verify signature failed.");
        
        // set auth:
        Authentication auth = new Authentication();
        auth.setIdentity(identity);
        auth.setEmail(request.getParameter("openid." + alias + ".value.email"));
        auth.setLanguage(request.getParameter("openid." + alias + ".value.language"));
        auth.setGender(request.getParameter("openid." + alias + ".value.gender"));
        auth.setFullname(getFullname(request, alias));
        auth.setFirstname(getFirstname(request, alias));
        auth.setLastname(getLastname(request, alias));
        return auth;
    }
}
