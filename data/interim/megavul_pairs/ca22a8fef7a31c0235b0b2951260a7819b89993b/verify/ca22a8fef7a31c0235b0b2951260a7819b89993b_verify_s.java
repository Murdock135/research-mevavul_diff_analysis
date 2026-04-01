class verify {
public boolean verify(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer(ISSUER)
                .build();
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.warn("verify jwt token failed " + e.getMessage());
            return false;
        }
    }
}
