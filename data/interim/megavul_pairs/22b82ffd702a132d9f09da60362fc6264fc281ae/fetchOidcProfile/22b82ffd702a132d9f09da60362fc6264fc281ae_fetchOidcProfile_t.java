class fetchOidcProfile {
private JWTClaimsSet fetchOidcProfile(BearerAccessToken accessToken) {
        final var userInfoRequest = new UserInfoRequest(configuration.findProviderMetadata().getUserInfoEndpointURI(),
            accessToken);
        final var userInfoHttpRequest = userInfoRequest.toHTTPRequest();
        configuration.configureHttpRequest(userInfoHttpRequest);
        try {
            final var httpResponse = userInfoHttpRequest.send();
            logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
                httpResponse.getContent());
            final var userInfoResponse = UserInfoResponse.parse(httpResponse);
            if (userInfoResponse instanceof UserInfoErrorResponse) {
                throw new TechnicalException("Bad User Info response, error="
                    + ((UserInfoErrorResponse) userInfoResponse).getErrorObject().toJSONObject());
            } else {
                final var userInfoSuccessResponse = (UserInfoSuccessResponse) userInfoResponse;
                final JWTClaimsSet userInfoClaimsSet;
                if (userInfoSuccessResponse.getUserInfo() != null) {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfo().toJWTClaimsSet();
                } else {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfoJWT().getJWTClaimsSet();
                }
                return userInfoClaimsSet;
            }
        } catch (IOException | ParseException | java.text.ParseException e) {
            throw new TechnicalException(e);
        }
    }
}
