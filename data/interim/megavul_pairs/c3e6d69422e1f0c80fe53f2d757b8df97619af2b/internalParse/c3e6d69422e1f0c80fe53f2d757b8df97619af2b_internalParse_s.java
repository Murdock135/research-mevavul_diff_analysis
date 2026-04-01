class internalParse {
@SuppressWarnings("checkstyle:emptyblock")
    private static List<Region> internalParse(
            final InputStream input,
            final boolean endpointVerification) throws IOException {

        Document document;
        try {

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            document = documentBuilder.parse(input);

        } catch (IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException("Unable to parse region metadata file: "
                    + exception.getMessage(),
                    exception);
        } finally {
            try {
                input.close();
            } catch (IOException exception) {
            }
        }

        NodeList regionNodes = document.getElementsByTagName(REGION_TAG);
        List<Region> regions = new ArrayList<Region>();
        for (int i = 0; i < regionNodes.getLength(); i++) {
            Node node = regionNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                regions.add(parseRegionElement(element, endpointVerification));
            }
        }

        return regions;
    }
}
