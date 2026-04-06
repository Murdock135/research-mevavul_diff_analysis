class isKeepAlive {
default boolean isKeepAlive() {
        return getFirst(CONNECTION, String.class).map(val -> val.equalsIgnoreCase("keep-alive")).orElse(false);
    }
}
