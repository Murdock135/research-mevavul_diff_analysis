class getRequestedFields {
@Override
    public PropertiesRequest getRequestedFields( InputStream in ) {
		final Set<QName> set = new LinkedHashSet<QName>();
        try {            
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            StreamUtils.readTo( in, bout, false, true );
            byte[] arr = bout.toByteArray();
            if( arr.length > 1 ) {
                ByteArrayInputStream bin = new ByteArrayInputStream( arr );
                XMLReader reader = XMLReaderFactory.createXMLReader();
				reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                PropFindSaxHandler handler = new PropFindSaxHandler();
                reader.setContentHandler( handler );
                try {
                    reader.parse( new InputSource( bin ) );
                    if( handler.isAllProp() ) {
                        return new PropertiesRequest();
                    } else {
                        set.addAll( handler.getAttributes().keySet() );
                    }
                } catch( IOException e ) {
                    log.warn( "exception parsing request body", e );
                    // ignore
                } catch( SAXException e ) {
                    log.warn( "exception parsing request body", e );
                    // ignore
                }
            }            
        } catch( Exception ex ) {
			// There's a report of an exception being thrown here by IT Hit Webdav client
			// Perhaps we can just log the error and return an empty set. Usually this
			// class is wrapped by the MsPropFindRequestFieldParser which will use a default
			// set of properties if this returns an empty set
			log.warn("Exception parsing PROPFIND request fields. Returning empty property set", ex);
            //throw new RuntimeException( ex );
        }
		return PropertiesRequest.toProperties(set);
    }
}
