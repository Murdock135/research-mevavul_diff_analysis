class find_1 {
public static @CheckForNull <T extends Descriptor> T find(Collection<? extends T> list, String className) {
        for (T d : list) {
            if(d.getClass().getName().equals(className))
                return d;
        }
        // Since we introduced Descriptor.getId(), it is a preferred method of identifying descriptor by a string.
        // To make that migration easier without breaking compatibility, let's also match up with the id.
        for (T d : list) {
            if(d.getId().equals(className))
                return d;
        }
        return null;
    }
}
