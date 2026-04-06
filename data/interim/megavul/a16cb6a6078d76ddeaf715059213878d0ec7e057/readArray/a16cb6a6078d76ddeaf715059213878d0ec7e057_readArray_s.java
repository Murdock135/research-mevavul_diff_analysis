class readArray {
private JsonArray readArray() throws IOException {
    read();
    JsonArray array=new JsonArray();
    skipWhiteSpace();
    if (readIf(']')) {
      return array;
    }
    do {
      skipWhiteSpace();
      array.add(readValue());
      skipWhiteSpace();
    } while (readIf(','));
    if (!readIf(']')) {
      throw expected("',' or ']'");
    }
    return array;
  }
}
