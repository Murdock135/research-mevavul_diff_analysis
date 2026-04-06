class readEnum {
@SuppressWarnings("unchecked")// For the Enum.valueOf call
    private Object readEnum(boolean unshared) throws OptionalDataException,
            ClassNotFoundException, IOException {
        // read classdesc for Enum first
        ObjectStreamClass classDesc = readEnumDesc();
        int newHandle = nextHandle();
        // read name after class desc
        String name;
        byte tc = nextTC();
        switch (tc) {
            case TC_REFERENCE:
                if (unshared) {
                    readNewHandle();
                    throw new InvalidObjectException("Unshared read of back reference");
                }
                name = (String) readCyclicReference();
                break;
            case TC_STRING:
                name = (String) readNewString(unshared);
                break;
            default:
                throw corruptStream(tc);
        }

        Enum<?> result;
        try {
            result = Enum.valueOf((Class) classDesc.forClass(), name);
        } catch (IllegalArgumentException e) {
            throw new InvalidObjectException(e.getMessage());
        }
        registerObjectRead(result, newHandle, unshared);
        return result;
    }
}
