class find {
public static @CheckForNull Descriptor find(String className) {
        return find(Jenkins.getInstance().getExtensionList(Descriptor.class),className);
    }
}
