class color2html {
@Nullable
  public static String color2html(@Nullable final Color color, final boolean hasAlpha) {
    String result = null;
    if (color != null) {
      final StringBuilder buffer = new StringBuilder();

      buffer.append('#');

      final int[] components;

      if (hasAlpha) {
        components = new int[]{color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue()};
      } else {
        components = new int[]{color.getRed(), color.getGreen(), color.getBlue()};
      }

      for (final int c : components) {
        final String str = Integer.toHexString(c & 0xFF).toUpperCase(Locale.ENGLISH);
        if (str.length() < 2) {
          buffer.append('0');
        }
        buffer.append(str);
      }

      result = buffer.toString();
    }
    return result;
  }
}
