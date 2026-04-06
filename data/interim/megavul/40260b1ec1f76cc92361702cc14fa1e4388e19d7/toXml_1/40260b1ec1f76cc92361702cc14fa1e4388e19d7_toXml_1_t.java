class toXml_1 {
public static Document toXml(final IBaseDataObject d) {
        final Element root = new Element("payload");
        root.addContent(JDOMUtil.protectedElement("name", d.getFilename()));
        final Element cf = new Element("current-forms");
        for (final String c : d.getAllCurrentForms()) {
            cf.addContent(JDOMUtil.simpleElement("current-form", c));
        }
        root.addContent(cf);
        root.addContent(JDOMUtil.simpleElement("encoding", d.getFontEncoding()));
        root.addContent(JDOMUtil.simpleElement("filetype", d.getFileType()));
        root.addContent(JDOMUtil.simpleElement("classification", d.getClassification()));
        final Element th = new Element("transform-history");
        for (final String s : d.transformHistory()) {
            th.addContent(JDOMUtil.simpleElement("itinerary-step", s));
        }
        root.addContent(th);
        if (d.getProcessingError() != null) {
            root.addContent(JDOMUtil.simpleElement("processing-error", d.getProcessingError()));
        }
        final Element meta = new Element("metadata");
        for (final String key : d.getParameters().keySet()) {
            final Element m = JDOMUtil.protectedElement("param", d.getStringParameter(key));
            m.setAttribute("name", key);
            meta.addContent(m);
        }
        root.addContent(meta);

        if (d.header() != null) {
            root.addContent(JDOMUtil.protectedElement("header", d.header()));
        }
        if (d.dataLength() > 0) {
            root.addContent(JDOMUtil.protectedElement("data", d.data()));
        }
        if (d.footer() != null) {
            root.addContent(JDOMUtil.protectedElement("footer", d.footer()));
        }

        // Alt views
        if (d.getNumAlternateViews() > 0) {
            final Element views = new Element("views");
            for (final String av : d.getAlternateViewNames()) {
                final Element v = JDOMUtil.protectedElement("view", d.getAlternateView(av));
                v.setAttribute("name", av);
                views.addContent(v);
            }
            root.addContent(views);
        }

        logger.debug("Produced xml document for " + d.shortName());
        return new Document(root);
    }
}
