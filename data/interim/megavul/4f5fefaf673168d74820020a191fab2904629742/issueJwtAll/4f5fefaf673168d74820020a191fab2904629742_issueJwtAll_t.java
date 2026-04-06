class issueJwtAll {
public static String issueJwtAll(String id, String subject, String issuer, Long period,
                                     String audience, String payload, Long notBefore,
                                     Map<String, Object> headerMap, Map<String, Object> customClaimMap){
        if (isUsedDefault) {
            throw new ExtSurenessException("Please config your custom jwt secret. JsonWebTokenUtil.setDefaultSecretKey | sureness.jwt.secret");
        }
        long currentTimeMillis = System.currentTimeMillis();
        JwtBuilder jwtBuilder = Jwts.builder();
        if (id != null) {
            jwtBuilder.setId(id);
        }
        if (subject != null) {
            jwtBuilder.setSubject(subject);
        }
        if (issuer != null) {
            jwtBuilder.setIssuer(issuer);
        }
        // set issue create time
        jwtBuilder.setIssuedAt(new Date(currentTimeMillis));
        // set expired time
        if (null != period) {
            jwtBuilder.setExpiration(new Date(currentTimeMillis + period * 1000));
        }
        if (null != audience) {
            jwtBuilder.setAudience(audience);
        }
        if (null != payload) {
            jwtBuilder.setPayload(payload);
        }
        if (null != notBefore){
            jwtBuilder.setNotBefore(new Date(notBefore * 1000));
        }
        if(null != headerMap) {
            jwtBuilder.setHeader(headerMap);
        }
        //claim param, eg: roles, perms, isRefresh
        if (null != customClaimMap) {
            customClaimMap.forEach(jwtBuilder::claim);
        }
        // compress，optional GZIP
        jwtBuilder.compressWith(CompressionCodecs.DEFLATE);
        // set secret key
        jwtBuilder.signWith(secretKey);
        return jwtBuilder.compact();
    }
}
