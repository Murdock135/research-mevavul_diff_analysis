class printFormattedMetadata {
public static String printFormattedMetadata(final IBaseDataObject payload) {
        final StringBuilder out = new StringBuilder();
        out.append(LS);
        for (final Map.Entry<String, Collection<Object>> entry : payload.getParameters().entrySet()) {
            out.append(entry.getKey()).append(SEP).append(entry.getValue()).append(LS);
        }
        return out.toString();
    }
}
