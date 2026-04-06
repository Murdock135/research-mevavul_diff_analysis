class parseElement {
public static Element parseElement(String xml) {
    try {
      DocumentBuilderFactory dbFactory = safeDocumentBuilderFactory();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
      return doc.getDocumentElement();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
