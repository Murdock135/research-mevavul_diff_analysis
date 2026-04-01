class parseContent {
private PropPatchParseResult parseContent( byte[] arr ) throws IOException, SAXException {
        if( arr.length > 0 ) {
            log.debug( "processing content" );
            ByteArrayInputStream bin = new ByteArrayInputStream( arr );
            XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			// https://www.owasp.org/index.php/XML_External_Entity_%28XXE%29_Processing
			reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			reader.setFeature("http://xml.org/sax/features/external-general-entities", false);

            PropPatchSaxHandler handler = new PropPatchSaxHandler();
            reader.setContentHandler( handler );
            reader.parse( new InputSource( bin ) );
            log.debug( "toset: " + handler.getAttributesToSet().size());
            return new PropPatchParseResult( handler.getAttributesToSet(), handler.getAttributesToRemove().keySet() );
        } else {
            log.debug( "empty content" );
            return new PropPatchParseResult( new HashMap<QName, String>(), new HashSet<QName>() );
        }

    }
}
