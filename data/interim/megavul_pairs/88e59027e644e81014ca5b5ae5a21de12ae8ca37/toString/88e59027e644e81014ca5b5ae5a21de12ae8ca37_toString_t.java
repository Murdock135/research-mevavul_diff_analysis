class toString {
public static String toString(InputStream inputStream) {
    try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
      return scanner.useDelimiter("\\A").next();
    }
  }
}
