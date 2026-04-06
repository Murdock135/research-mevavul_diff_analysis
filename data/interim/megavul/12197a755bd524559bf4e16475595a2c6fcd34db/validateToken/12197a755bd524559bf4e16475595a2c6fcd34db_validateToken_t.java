class validateToken {
public Claims validateToken(String token) throws AuthenticationException {
        try {
            RsaKeyUtil rsaKeyUtil = new RsaKeyUtil();
            PublicKey publicKey = rsaKeyUtil.fromPemEncoded(keycloakPublicKey);

            return Jwts.parser().setSigningKey(publicKey).parseJws(token.replace("Bearer ", "")).getBody();
        } catch (Exception e){
            throw new AuthenticationException(String.format("Failed to check user authorization for token: %s", token), e);
        }
    }
}
