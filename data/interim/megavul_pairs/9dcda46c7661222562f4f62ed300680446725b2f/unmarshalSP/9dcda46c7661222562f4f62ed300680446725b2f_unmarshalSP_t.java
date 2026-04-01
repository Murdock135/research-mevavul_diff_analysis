class unmarshalSP {
private ServiceProvider unmarshalSP(SpFileContent spFileContent, String tenantDomain)
            throws IdentityApplicationManagementException {

        if (StringUtils.isEmpty(spFileContent.getContent())) {
            throw new IdentityApplicationManagementException(String.format("Empty Service Provider configuration file" +
                    " %s uploaded by tenant: %s", spFileContent.getFileName(), tenantDomain));
        }
        try {
            // Creating secure parser by disabling XXE.
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setXIncludeAware(false);
            try {
                spf.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
                spf.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
                spf.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
                spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            } catch (SAXException | ParserConfigurationException e) {
                log.error("Failed to load XML Processor Feature " + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE + " or "
                        + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE + " or " + Constants.LOAD_EXTERNAL_DTD_FEATURE
                        + " or secure-processing.");
            }
            // Creating source object using the secure parser.
            Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(),
                    new InputSource(new StringReader(spFileContent.getContent())));
            // Performing unmarshall operation by passing the generated source object to the unmarshaller.
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceProvider.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (ServiceProvider) unmarshaller.unmarshal(xmlSource);
        } catch (JAXBException | SAXException | ParserConfigurationException e) {
            throw new IdentityApplicationManagementException(String.format("Error in reading Service Provider " +
                    "configuration file %s uploaded by tenant: %s", spFileContent.getFileName(), tenantDomain), e);
        }
    }
}
