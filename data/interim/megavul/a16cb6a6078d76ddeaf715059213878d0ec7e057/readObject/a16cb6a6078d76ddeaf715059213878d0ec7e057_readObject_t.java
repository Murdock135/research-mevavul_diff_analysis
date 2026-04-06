class readObject {
private JsonObject readObject(int depth) throws IOException {
    read();
    JsonObject object=new JsonObject();
    skipWhiteSpace();
    if (readIf('}')) {
      return object;
    }
    do {
      skipWhiteSpace();
      String name=readName();
      skipWhiteSpace();
      if (!readIf(':')) {
        throw expected("':'");
      }
      skipWhiteSpace();
      object.add(name, readValue(depth));
      skipWhiteSpace();
    } while (readIf(','));
    if (!readIf('}')) {
      throw expected("',' or '}'");
    }
    return object;
  }
}
