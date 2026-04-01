class readValue {
private JsonValue readValue(int depth) throws IOException, ParseException {
    if(current==123) {
      ++depth;
    }
       /* The following has been refrenced for the resolution of the vulnerability:
    https://github.com/FasterXML/jackson-databind/commit/fcfc4998ec23f0b1f7f8a9521c2b317b6c25892b
    */
    if(depth>MAX_DEPTH) {
      throw error("The passed json has exhausted the depth supported of "+MAX_DEPTH+".");
    }
    switch(current) {
      case '\'':
      case '"': return readString();
      case '[': return readArray(depth);
      case '{': return readObject(false, depth);
      default: return readTfnns();
    }

  }
}
