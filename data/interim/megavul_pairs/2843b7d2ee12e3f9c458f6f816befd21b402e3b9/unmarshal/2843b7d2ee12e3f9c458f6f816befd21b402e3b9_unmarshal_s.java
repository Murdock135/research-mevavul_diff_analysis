class unmarshal {
@Override
	public Object unmarshal(Source source, MimeContainer mimeContainer) throws XmlMappingException {
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			if (this.mtomEnabled && mimeContainer != null) {
				unmarshaller.setAttachmentUnmarshaller(new Jaxb2AttachmentUnmarshaller(mimeContainer));
			}
			if (StaxUtils.isStaxSource(source)) {
				return unmarshalStaxSource(unmarshaller, source);
			}
			else if (this.mappedClass != null) {
				return unmarshaller.unmarshal(source, this.mappedClass).getValue();
			}
			else {
				return unmarshaller.unmarshal(source);
			}
		}
		catch (JAXBException ex) {
			throw convertJaxbException(ex);
		}
	}
}
