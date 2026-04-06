class escapeJavaScript {
public static String escapeJavaScript(String source) {

        source = CmsStringUtil.substitute(source, "\\", "\\\\");
        source = CmsStringUtil.substitute(source, "\"", "\\\"");
        source = CmsStringUtil.substitute(source, "\'", "\\\'");
        source = CmsStringUtil.substitute(source, "\r\n", "\\n");
        source = CmsStringUtil.substitute(source, "\n", "\\n");

        // to avoid XSS (closing script tags) in embedded Javascript 
        source = CmsStringUtil.substitute(source, "/", "\\/");
        return source;
    }
}
