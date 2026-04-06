class compact {
public String compact() {
    return id.replaceAll("/", "-").replaceAll("\\\\", "-");
  }
}
