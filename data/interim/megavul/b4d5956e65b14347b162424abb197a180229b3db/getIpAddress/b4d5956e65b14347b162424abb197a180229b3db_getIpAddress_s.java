class getIpAddress {
public static String getIpAddress(HttpServletRequest request) {
        if (null != request) {
            String ip = request.getHeader("X-Real-IP");
            if (CommonUtils.notEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
            ip = request.getHeader("X-Forwarded-For");
            if (CommonUtils.notEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(Constants.COMMA);
                if (index != -1) {
                    return ip.substring(0, index);
                }
                return ip;
            }
            return request.getRemoteAddr();
        }
        return null;
    }
}
