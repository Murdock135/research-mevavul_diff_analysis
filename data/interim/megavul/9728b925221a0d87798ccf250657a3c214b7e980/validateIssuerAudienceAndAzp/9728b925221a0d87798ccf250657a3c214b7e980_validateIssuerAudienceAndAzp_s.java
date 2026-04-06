class validateIssuerAudienceAndAzp {
protected boolean validateIssuerAudienceAndAzp(@NonNull JwtClaims claims,
                                                   @NonNull String iss,
                                                   @NonNull List<String> audiences,
                                                   @NonNull String clientId,
                                                   @NonNull OpenIdClientConfiguration openIdClientConfiguration) {
        if (openIdClientConfiguration.getIssuer().isPresent()) {
            Optional<URL> issuerOptional = openIdClientConfiguration.getIssuer();
            if (issuerOptional.isPresent()) {
                String issuer = issuerOptional.get().toString();
                return issuer.equalsIgnoreCase(iss) ||
                        audiences.contains(clientId) &&
                                validateAzp(claims, clientId, audiences);
            }
        }
        return false;
    }
}
