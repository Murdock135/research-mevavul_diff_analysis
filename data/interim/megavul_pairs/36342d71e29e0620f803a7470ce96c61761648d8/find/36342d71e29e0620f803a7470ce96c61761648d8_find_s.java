class find {
public static Descriptor find(String className) {
        return find(Jenkins.getInstance().getExtensionList(Descriptor.class),className);
    }
}
