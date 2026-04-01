class contains {
default boolean contains(String name) {
        return get(name, Object.class).isPresent();
    }
}
