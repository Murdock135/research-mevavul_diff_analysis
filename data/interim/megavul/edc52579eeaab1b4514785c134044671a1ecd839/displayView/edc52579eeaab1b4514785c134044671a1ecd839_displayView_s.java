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
            StringBuffer result = new StringBuffer();
            super.displayView(result, name, prefix, object, context);
            if (getObjectDocumentSyntax(object, context).equals(Syntax.XWIKI_1_0)) {
                buffer.append(context.getWiki().parseContent(result.toString(), context));
            } else {
                // Don't do anything since this mode is deprecated and not supported in the new rendering.
                buffer.append(result);
            }
        } else {
            BaseProperty property = (BaseProperty) object.safeget(name);
            if (property != null) {
                String content = property.toText();
                XWikiDocument sdoc = getObjectDocument(object, context);

                if (sdoc != null) {
                    if (contentType == ContentType.VELOCITYWIKI) {
                        // Start with a pass of Velocity
                        // TODO: maybe make velocity+wiki a syntax so that getRenderedContent can directly take care
                        // of that
                        VelocityEvaluator velocityEvaluator = Utils.getComponent(VelocityEvaluator.class);
                        content = velocityEvaluator.evaluateVelocityNoException(content,
                            isolated ? sdoc.getDocumentReference() : null);
                    }

                    // Make sure the right author is used to execute the textarea
                    // Clone the document to void messaging with the cache
                    if (!Objects.equals(sdoc.getAuthors().getEffectiveMetadataAuthor(),
                        sdoc.getAuthors().getContentAuthor())) {
                        sdoc = sdoc.clone();
                        sdoc.getAuthors().setContentAuthor(sdoc.getAuthors().getEffectiveMetadataAuthor());
                    }

                    buffer.append(context.getDoc().getRenderedContent(content, sdoc.getSyntax(), isRestricted(), sdoc,
                        isolated, context));
                } else {
                    buffer.append(content);
                }
            }
        }
    }
}
