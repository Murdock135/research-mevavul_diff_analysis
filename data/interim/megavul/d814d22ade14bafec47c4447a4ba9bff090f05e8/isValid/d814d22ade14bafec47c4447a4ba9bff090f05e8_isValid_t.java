class isValid {
@Override
  public RequestParameter isValid(String value) throws ValidationException {
    try {
      DocumentBuilder parser = createDocumentBuilderFactoryInstance().newDocumentBuilder();
      Document document = parser.parse(value);
      this.schemaValidator.validate(new DOMSource(document));
      return RequestParameter.create(document);
    } catch (Exception e) {
      throw ValidationException.ValidationExceptionFactory.generateInvalidXMLBodyException(e.getMessage());
    }
  }
}
