class parseElement_1 {
public static Element parseElement(String xml) {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
      return doc.getDocumentElement();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
