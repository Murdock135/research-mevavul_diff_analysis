class getDefaultCleanerTransformations {
private TrimAttributeCleanerTransformations getDefaultCleanerTransformations(HTMLCleanerConfiguration configuration)
    {
        TrimAttributeCleanerTransformations defaultTransformations = new TrimAttributeCleanerTransformations();

        TagTransformation tt;

        // note that we do not care here to use a TrimAttributeTagTransformation, since the attributes are not preserved
        if (!isHTML5(configuration)) {
            // These tags are not obsolete in HTML5.
            tt = new TagTransformation(HTMLConstants.TAG_B, HTMLConstants.TAG_STRONG, false);
            defaultTransformations.addTransformation(tt);

            tt = new TagTransformation(HTMLConstants.TAG_I, HTMLConstants.TAG_EM, false);
            defaultTransformations.addTransformation(tt);

            tt = new TagTransformation(HTMLConstants.TAG_U, HTMLConstants.TAG_INS, false);
            defaultTransformations.addTransformation(tt);

            tt = new TagTransformation(HTMLConstants.TAG_S, HTMLConstants.TAG_DEL, false);
            defaultTransformations.addTransformation(tt);
        }

        tt = new TagTransformation(HTMLConstants.TAG_STRIKE, HTMLConstants.TAG_DEL, false);
        defaultTransformations.addTransformation(tt);

        tt = new TagTransformation(HTMLConstants.TAG_CENTER, HTMLConstants.TAG_P, false);
        tt.addAttributeTransformation(HTMLConstants.ATTRIBUTE_STYLE, "text-align:center");
        defaultTransformations.addTransformation(tt);

        if (isHTML5(configuration)) {
            // Font tags are removed before the filters are applied in HTML5, we thus need a transformation here.
            defaultTransformations.addTransformation(new FontTagTransformation());

            // The tt-tag is obsolete in HTML5
            tt = new TrimAttributeTagTransformation(HTMLConstants.TAG_TT, HTMLConstants.TAG_SPAN);
            tt.addAttributeTransformation(HTMLConstants.ATTRIBUTE_CLASS, "${class} monospace");
            defaultTransformations.addTransformation(tt);
        }

        if (isRestricted(configuration)) {

            tt = new TagTransformation(HTMLConstants.TAG_SCRIPT, HTMLConstants.TAG_PRE, false);
            defaultTransformations.addTransformation(tt);

            tt = new TagTransformation(HTMLConstants.TAG_STYLE, HTMLConstants.TAG_PRE, false);
            defaultTransformations.addTransformation(tt);
        }

        return defaultTransformations;
    }
}
