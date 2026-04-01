class extractCorsOrigin {
public String extractCorsOrigin(String pOrigin) {
        if (pOrigin != null) {
            // Prevent HTTP response splitting attacks
            String origin  = pOrigin.replaceAll("[\\n\\r]*","");
            if (backendManager.isOriginAllowed(origin,false)) {
                return "null".equals(origin) ? "*" : origin;
            } else {
                return null;
            }
        }
        return null;
    }
}
