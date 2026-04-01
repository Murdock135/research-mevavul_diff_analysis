class write {
public void write(final Writer writer, final String entityType) throws IOException {
        try {
            final SAXTransformerFactory stf = (SAXTransformerFactory) TransformerFactory.newInstance();
            final TransformerHandler th = stf.newTransformerHandler();
            final Transformer transformer = th.getTransformer();

            writer.write("<?xml version=\"1.1\" encoding=\"UTF-8\"?>\n");
            XMLUtil.setCommonOutputProperties(transformer, true);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, YES);

            th.setResult(new StreamResult(writer));
            th.startDocument();

            final String xmlName = XMLUtil.toXMLName(entityType);

            writeStartElement(th, xmlName);

            for (final Entry<String, List<Object>> entry : propertyValues.entrySet()) {
                final String propertyName = entry.getKey();
                final List<Object> list = entry.getValue();

                for (final Object value : list) {
                    if (value != null) {
                        if (value instanceof Collection) {
                            for (final Object valueItem : (Collection<?>) value) {
                                writeStartElement(th, propertyName);
                                writeContent(th, String.valueOf(valueItem));
                                writeEndElement(th, propertyName);
                            }
                        } else if (value instanceof DocRef) {
                            final DocRef docRef = (DocRef) value;

                            writeStartElement(th, propertyName);

                            writeStartElement(th, "doc");
                            writeStartElement(th, "type");
                            writeContent(th, docRef.getType());
                            writeEndElement(th, "type");
                            writeStartElement(th, "uuid");
                            writeContent(th, docRef.getUuid());
                            writeEndElement(th, "uuid");
                            writeStartElement(th, "name");
                            writeContent(th, docRef.getName());
                            writeEndElement(th, "name");
                            writeEndElement(th, "doc");

                            writeEndElement(th, propertyName);
                        } else if (value instanceof Date) {
                            writeStartElement(th, propertyName);
                            writeContent(th, DateUtil.createNormalDateTimeString(((Date) value).getTime()));
                            writeEndElement(th, propertyName);

                        } else {
                            writeStartElement(th, propertyName);
                            writeContent(th, String.valueOf(value));
                            writeEndElement(th, propertyName);
                        }
                    }
                }
            }

            writeEndElement(th, xmlName);
            th.endDocument();

            writer.close();

        } catch (final IOException e) {
            throw e;
        } catch (final Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
