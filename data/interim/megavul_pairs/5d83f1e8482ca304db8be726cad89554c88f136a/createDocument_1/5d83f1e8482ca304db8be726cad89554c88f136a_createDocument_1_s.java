class createDocument_1 {
public static Document createDocument() {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
      return doc;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
