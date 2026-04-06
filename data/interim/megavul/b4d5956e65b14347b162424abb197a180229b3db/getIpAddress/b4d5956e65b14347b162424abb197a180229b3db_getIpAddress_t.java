class getIpAddress {
public static String getIpAddress(HttpServletRequest request) {
        if (null != request) {
            String localIp = request.getRemoteAddr();
            String ip = request.getHeader("X-Real-IP");
            if (CommonUtils.notEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.length() > 64) {
                    ip = ip.substring(0, 64);
                }
                return ip.equals(localIp) ? ip : ip + "," + localIp;
            }
            ip = request.getHeader("X-Forwarded-For");
            if (CommonUtils.notEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(Constants.COMMA);
                if (index != -1) {
                    return ip.substring(0, index);
                }
                if (ip.length() > 64) {
                    ip = ip.substring(0, 64);
                }
                return ip.equals(localIp) ? ip : ip + "," + localIp;
            }
            return localIp;
        }
        return null;
    }
}
