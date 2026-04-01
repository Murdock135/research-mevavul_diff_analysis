class readValue {
private JsonValue readValue(int depth) throws IOException {
    /* The following has been refrenced for the resolution of the vulnerability:
    https://github.com/FasterXML/jackson-databind/commit/fcfc4998ec23f0b1f7f8a9521c2b317b6c25892b
    */
    if(depth>MAX_DEPTH) {
      throw error("The passed json has exhausted the maximum supported depth of "+MAX_DEPTH+".");
    }
    switch(current) {
      case 'n':
        return readNull();
      case 't':
        return readTrue();
      case 'f':
        return readFalse();
      case '"':
        return readString();
      case '[':
        return readArray(depth + 1);
      case '{':
        return readObject(depth + 1);
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return readNumber();
      default:
        throw expected("value");
    }
  }
}
