class beginFormat {
@Override
    public void beginFormat(Format format, Map<String, String> parameters)
    {
        // Right now, the only difference with the super class is about the "monospace" format
        if (format == Format.MONOSPACE) {
            Map<String, String> attributes = new LinkedHashMap<>(parameters);
            String cssClass = "monospace";
            // The element may already have a class
            if (attributes.containsKey(PROP_CLASS)) {
                cssClass = String.format("%s %s", cssClass, attributes.get(PROP_CLASS));
            }
            attributes.put(PROP_CLASS, cssClass);
            getXHTMLWikiPrinter().printXMLStartElement(ELEM_SPAN, attributes);
        } else {
            // Call the super class
            super.beginFormat(format, parameters);
        }

    }
}
