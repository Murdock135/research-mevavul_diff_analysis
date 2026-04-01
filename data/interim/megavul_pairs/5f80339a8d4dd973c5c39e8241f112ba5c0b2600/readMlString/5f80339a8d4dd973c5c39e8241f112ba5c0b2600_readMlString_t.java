class readMlString {
private String readMlString() throws IOException {

    // Parse a multiline string value.
    StringBuilder sb=new StringBuilder();
    int triple=0;

    // we are at '''
    int indent=index-lineOffset-4;

    // skip white/to (newline)
    for (; ; ) {
      if (isWhiteSpace(current) && current!='\n') read();
      else break;
    }
    if (current=='\n') { read(); skipIndent(indent); }

    // When parsing for string values, we must look for " and \ characters.
    while (true) {
      if (current<0) throw error("Bad multiline string");
      else if (current=='\'') {
        triple++;
        read();
        if (triple==3) {
          if (sb.length() > 0 && sb.charAt(sb.length()-1)=='\n') sb.deleteCharAt(sb.length()-1);

          return sb.toString();
        }
        else continue;
      }
      else {
        while (triple>0) {
          sb.append('\'');
          triple--;
        }
      }
      if (current=='\n') {
        sb.append('\n');
        read();
        skipIndent(indent);
      }
      else {
        if (current!='\r') sb.append((char)current);
        read();
      }
    }
  }
}
