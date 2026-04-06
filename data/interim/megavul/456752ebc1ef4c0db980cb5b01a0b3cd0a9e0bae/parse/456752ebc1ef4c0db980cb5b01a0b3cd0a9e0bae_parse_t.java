class parse {
public Object parse(SerializerHandler serializerHandler, InputStream response, boolean debugMode) throws XMLRPCException {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			// Ensure the xml parser won't allow exploitation of the vuln CWE-611
			// (described on https://cwe.mitre.org/data/definitions/611.html )
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setExpandEntityReferences(false);
			factory.setNamespaceAware(true);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);
			// End of the configuration of the parser for CWE-611

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(response);
			if (debugMode ){
				printDocument(dom, System.out);
			}
			Element e = dom.getDocumentElement();


			// Check for root tag
			if(!e.getNodeName().equals(XMLRPCClient.METHOD_RESPONSE)) {
				throw new XMLRPCException("MethodResponse root tag is missing.");
			}

			e = XMLUtil.getOnlyChildElement(e.getChildNodes());

			if(e.getNodeName().equals(XMLRPCClient.PARAMS)) {

				e = XMLUtil.getOnlyChildElement(e.getChildNodes());

				if(!e.getNodeName().equals(XMLRPCClient.PARAM)) {
					throw new XMLRPCException("The params tag must contain a param tag.");
				}

				return getReturnValueFromElement(serializerHandler, e);

			} else if(e.getNodeName().equals(XMLRPCClient.FAULT)) {

				@SuppressWarnings("unchecked")
				Map<String,Object> o = (Map<String,Object>)getReturnValueFromElement(serializerHandler, e);

				throw new XMLRPCServerException((String)o.get(FAULT_STRING), (Integer)o.get(FAULT_CODE));

			}

			throw new XMLRPCException("The methodResponse tag must contain a fault or params tag.");

		} catch(XMLRPCServerException e) {
			throw e;
		} catch (Exception ex) {
			throw new XMLRPCException("Error getting result from server.", ex);
		}

	}
}
