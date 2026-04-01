class verifyResponse {
public static boolean verifyResponse(Response response,
    String requestUrl, HttpServletRequest request) {
        if(!response.isSigned()) {
            debug.message("verifyResponse: Response is not signed");
            return false;
        }
        if (!response.isSignatureValid()) {
            debug.message("verifyResponse: Response's signature is invalid.");
            return false;
        }

        // check Recipient == this server's POST profile URL(requestURL)
        String recipient = response.getRecipient();
        if ((recipient == null) || (recipient.length() == 0) ||
        ((!equalURL(recipient, requestUrl)) &&
        (!equalURL(recipient,getLBURL(requestUrl, request))))) {
            debug.error("verifyResponse : Incorrect Recipient.");
            return false;
        }
        
        // check status of the Response
        if (!response.getStatus().getStatusCode().getValue().endsWith(
        SAMLConstants.STATUS_CODE_SUCCESS_NO_PREFIX)) {
            debug.error("verifyResponse : Incorrect StatusCode value.");
            return false;
        }
        
        return true;
    }
}
