class processResponse {
public static Map processResponse(Response samlResponse, String target) 
        throws SAMLException {
        List assertions = null;    
        SAMLServiceManager.SOAPEntry partnerdest = null;
        Subject assertionSubject = null;
        if (samlResponse.isSigned()) {
            // verify the signature
            boolean isSignedandValid = verifySignature(samlResponse);
            if (!isSignedandValid) {
                throw new SAMLException(bundle.getString("invalidResponse"));
            }
        }
        // check Assertion and get back a Map of relevant data including,
        // Subject, SOAPEntry for the partner and the List of Assertions.
        Map ssMap = verifyAssertionAndGetSSMap(samlResponse);
        if (debug.messageEnabled()) {
            debug.message("processResponse: ssMap = " + ssMap);
        }
        
        if (ssMap == null) {
            throw new SAMLException(bundle.getString("invalidAssertion"));
        }
        assertionSubject = (com.sun.identity.saml.assertion.Subject)
            ssMap.get(SAMLConstants.SUBJECT);
        if (assertionSubject == null) {
            throw new SAMLException(bundle.getString("nullSubject"));
        }
        
        partnerdest = (SAMLServiceManager.SOAPEntry)ssMap
            .get(SAMLConstants.SOURCE_SITE_SOAP_ENTRY);
        if (partnerdest == null) {
            throw new SAMLException(bundle.getString("failedAccountMapping"));
        }
        
        assertions = (List)ssMap.get(SAMLConstants.POST_ASSERTION);
        Map sessMap = null;
        try { 
            sessMap = getAttributeMap(partnerdest, assertions,
                assertionSubject, target); 
        } catch (Exception se) {
            debug.error("SAMLUtils.processResponse :" , se);
            throw new SAMLException(
                bundle.getString("failProcessResponse"));
        }
        return sessMap;
    }
}
