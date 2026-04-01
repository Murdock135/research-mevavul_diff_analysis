class escapeJavaScript {
public static String escapeJavaScript(String source) {

        source = CmsStringUtil.substitute(source, "\\", "\\\\");
        source = CmsStringUtil.substitute(source, "\"", "\\\"");
        source = CmsStringUtil.substitute(source, "\'", "\\\'");
        source = CmsStringUtil.substitute(source, "\r\n", "\\n");
        source = CmsStringUtil.substitute(source, "\n", "\\n");
        return source;
    }
}
