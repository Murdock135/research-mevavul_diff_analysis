class checkSignResponse {
public static SignResponseVerificationResult checkSignResponse(String signResponseMessage,
			DigitalSignatureServiceSession session) throws JAXBException, ParserConfigurationException, SAXException,
			IOException, MarshalException, XMLSignatureException, Base64DecodingException, UserCancelException,
			ClientRuntimeException, SubjectNotAuthorizedException {
		if (null == session) {
			throw new IllegalArgumentException("missing session");
		}

		byte[] decodedSignResponseMessage;
		try {
			decodedSignResponseMessage = Base64.decode(signResponseMessage);
		} catch (Base64DecodingException e) {
			throw new SecurityException("no Base64");
		}

		// DOM parsing
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream signResponseInputStream = new ByteArrayInputStream(decodedSignResponseMessage);
		Document signResponseDocument;
		try {
			signResponseDocument = documentBuilder.parse(signResponseInputStream);
		} catch (SAXParseException e) {
			throw new SecurityException("no valid SignResponse XML");
		}

		// JAXB parsing
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class,
				be.e_contract.dssp.ws.jaxb.dss.async.ObjectFactory.class,
				be.e_contract.dssp.ws.jaxb.wsa.ObjectFactory.class, be.e_contract.dssp.ws.jaxb.wsu.ObjectFactory.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		SignResponse signResponse;
		try {
			signResponse = (SignResponse) unmarshaller.unmarshal(signResponseDocument);
		} catch (UnmarshalException e) {
			throw new SecurityException("no valid SignResponse XML");
		}

		// signature verification
		NodeList signatureNodeList = signResponseDocument.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#",
				"Signature");
		if (signatureNodeList.getLength() != 1) {
			throw new SecurityException("requires 1 ds:Signature element");
		}
		Element signatureElement = (Element) signatureNodeList.item(0);
		SecurityTokenKeySelector keySelector = new SecurityTokenKeySelector(session.getKey());
		DOMValidateContext domValidateContext = new DOMValidateContext(keySelector, signatureElement);
		XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
		XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(domValidateContext);
		boolean validSignature = xmlSignature.validate(domValidateContext);
		if (false == validSignature) {
			throw new SecurityException("invalid ds:Signature");
		}

		// verify content
		String responseId = null;
		RelatesToType relatesTo = null;
		AttributedURIType to = null;
		TimestampType timestamp = null;
		String signerIdentity = null;
		AnyType optionalOutputs = signResponse.getOptionalOutputs();
		List<Object> optionalOutputsList = optionalOutputs.getAny();
		for (Object optionalOutputObject : optionalOutputsList) {
			LOGGER.debug("optional output object type: {}", optionalOutputObject.getClass().getName());
			if (optionalOutputObject instanceof JAXBElement) {
				JAXBElement optionalOutputElement = (JAXBElement) optionalOutputObject;
				LOGGER.debug("optional output name: {}", optionalOutputElement.getName());
				LOGGER.debug("optional output value type: {}", optionalOutputElement.getValue().getClass().getName());
				if (RESPONSE_ID_QNAME.equals(optionalOutputElement.getName())) {
					responseId = (String) optionalOutputElement.getValue();
				} else if (optionalOutputElement.getValue() instanceof RelatesToType) {
					relatesTo = (RelatesToType) optionalOutputElement.getValue();
				} else if (TO_QNAME.equals(optionalOutputElement.getName())) {
					to = (AttributedURIType) optionalOutputElement.getValue();
				} else if (optionalOutputElement.getValue() instanceof TimestampType) {
					timestamp = (TimestampType) optionalOutputElement.getValue();
				} else if (optionalOutputElement.getValue() instanceof NameIdentifierType) {
					NameIdentifierType nameIdentifier = (NameIdentifierType) optionalOutputElement.getValue();
					signerIdentity = nameIdentifier.getValue();
				}
			}
		}

		Result result = signResponse.getResult();
		LOGGER.debug("result major: {}", result.getResultMajor());
		LOGGER.debug("result minor: {}", result.getResultMinor());
		if (DigitalSignatureServiceConstants.REQUESTER_ERROR_RESULT_MAJOR.equals(result.getResultMajor())) {
			if (DigitalSignatureServiceConstants.USER_CANCEL_RESULT_MINOR.equals(result.getResultMinor())) {
				throw new UserCancelException();
			}
			if (DigitalSignatureServiceConstants.CLIENT_RUNTIME_RESULT_MINOR.equals(result.getResultMinor())) {
				throw new ClientRuntimeException();
			}
			if (DigitalSignatureServiceConstants.SUBJECT_NOT_AUTHORIZED_RESULT_MINOR.equals(result.getResultMinor())) {
				throw new SubjectNotAuthorizedException(signerIdentity);
			}
		}
		if (false == DigitalSignatureServiceConstants.PENDING_RESULT_MAJOR.equals(result.getResultMajor())) {
			throw new SecurityException("invalid dss:ResultMajor");
		}

		if (null == responseId) {
			throw new SecurityException("missing async:ResponseID");
		}
		if (false == responseId.equals(session.getResponseId())) {
			throw new SecurityException("invalid async:ResponseID");
		}

		if (null == relatesTo) {
			throw new SecurityException("missing wsa:RelatesTo");
		}
		if (false == session.getInResponseTo().equals(relatesTo.getValue())) {
			throw new SecurityException("invalid wsa:RelatesTo");
		}

		if (null == to) {
			throw new SecurityException("missing wsa:To");
		}
		if (false == session.getDestination().equals(to.getValue())) {
			throw new SecurityException("invalid wsa:To");
		}

		if (null == timestamp) {
			throw new SecurityException("missing wsu:Timestamp");
		}
		AttributedDateTime expires = timestamp.getExpires();
		if (null == expires) {
			throw new SecurityException("missing wsu:Timestamp/wsu:Expires");
		}
		DateTime expiresDateTime = new DateTime(expires.getValue());
		DateTime now = new DateTime();
		if (now.isAfter(expiresDateTime)) {
			throw new SecurityException("wsu:Timestamp expired");
		}

		session.setSignResponseVerified(true);

		SignResponseVerificationResult signResponseVerificationResult = new SignResponseVerificationResult(
				signerIdentity);
		return signResponseVerificationResult;
	}
}
