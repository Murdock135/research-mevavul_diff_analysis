class isDataFlavorAvailable {
public static boolean isDataFlavorAvailable(@Nonnull final Clipboard clipboard, @Nonnull final DataFlavor flavor) {
    boolean result = false;
    try {
      result = clipboard.isDataFlavorAvailable(flavor);
    } catch (final IllegalStateException ex) {
      LOGGER.warn("Can't get access to clipboard : " + ex.getMessage());
    }
    return result;
  }
}
