class isKeepAlive {
default boolean isKeepAlive() {
        return getFirst(CONNECTION, ConversionContext.STRING).map(val -> val.equalsIgnoreCase("keep-alive")).orElse(false);
    }
}
