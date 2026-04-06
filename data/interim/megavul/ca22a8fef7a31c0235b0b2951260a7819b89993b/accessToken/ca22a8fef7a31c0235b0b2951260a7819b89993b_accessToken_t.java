class accessToken {
public String accessToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);

        return JWT.create()
                .withExpiresAt(new Date(new Date().getTime() + ACCESS_EXPIRE_TIME))
                .withIssuer(ISSUER)
                .withClaim("username", username)
                .sign(algorithm);
    }
}
