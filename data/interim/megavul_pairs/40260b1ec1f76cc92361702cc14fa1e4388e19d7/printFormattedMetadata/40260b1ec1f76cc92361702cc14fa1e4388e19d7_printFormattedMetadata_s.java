class printFormattedMetadata {
public static String printFormattedMetadata(final IBaseDataObject payload) {
        final StringBuilder out = new StringBuilder();
        out.append(LS);
        for (final Map.Entry<String, Collection<Object>> entry : payload.getParameters().entrySet()) {
            out.append(entry.getKey() + SEP + entry.getValue() + LS);
        }
        return out.toString();
    }
}
