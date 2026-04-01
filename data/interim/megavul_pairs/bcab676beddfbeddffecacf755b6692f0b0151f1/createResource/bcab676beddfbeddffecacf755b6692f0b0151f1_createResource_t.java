class createResource {
@Override
    public Resource createResource(URI uri) {
        ArchimateResource resource = new ArchimateResource(uri);
        
        // Ensure we have ExtendedMetaData for both Saving and Loading
        ExtendedMetaData ext = new ConverterExtendedMetadata();

        resource.getDefaultLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, ext);
        resource.getDefaultSaveOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, ext);

        resource.getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
        resource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
        
        resource.getDefaultLoadOptions().put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        resource.setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());
        
        // Don't allow loading an unregistered URI in case of exploits
        resource.getDefaultLoadOptions().put(XMLResource.OPTION_USE_PACKAGE_NS_URI_AS_LOCATION, false);
        
        Map<String, Object> parserFeatures = new HashMap<String, Object>();
        // Don't allow DTD loading in case of XSS exploits
        parserFeatures.put("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE); //$NON-NLS-1$
        parserFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE); //$NON-NLS-1$
        parserFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.FALSE); //$NON-NLS-1$
        parserFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE); //$NON-NLS-1$
        resource.getDefaultLoadOptions().put(XMLResource.OPTION_PARSER_FEATURES, parserFeatures);
        
        // Not sure about this
        // resource.getDefaultSaveOptions().put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);

        // Don't set this as it prefixes a hash # to ID references
        // resource.getDefaultLoadOptions().put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
        // resource.getDefaultSaveOptions().put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);

        // Not sure about this
        // resource.getDefaultLoadOptions().put(XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);
        
        return resource;
    }
}
