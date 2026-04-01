class createDocument_1 {
public static Document createDocument() {
    try {
      DocumentBuilderFactory dbFactory = safeDocumentBuilderFactory();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
      return doc;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
