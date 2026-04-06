class displayView {
@Override
    public void displayView(StringBuffer buffer, String name, String prefix, BaseCollection object, boolean isolated,
        XWikiContext context)
    {
        String contentTypeString = getContentType();
        ContentType contentType = ContentType.getByValue(contentTypeString);

        if (contentType == ContentType.PURE_TEXT) {
            super.displayView(buffer, name, prefix, object, context);
        } else if (contentType == ContentType.VELOCITY_CODE) {
            displayVelocityCode(buffer, name, prefix, object, context);
        } else {
            BaseProperty<?> property = (BaseProperty<?>) object.safeget(name);
            if (property != null) {
                String content = property.toText();
                XWikiDocument sdoc = getObjectDocument(object, context);

                if (contentType == ContentType.VELOCITYWIKI) {
                    content = maybeEvaluateContent(name, isolated, content, sdoc);
                }

                if (sdoc != null) {
                    sdoc = ensureContentAuthorIsMetadataAuthor(sdoc);

                    buffer.append(
                        context.getDoc().getRenderedContent(content, sdoc.getSyntax(), isRestricted(), sdoc,
                            isolated, context));
                } else {
                    buffer.append(XMLUtils.escapeElementText(content));
                }
            }
        }
    }
}
