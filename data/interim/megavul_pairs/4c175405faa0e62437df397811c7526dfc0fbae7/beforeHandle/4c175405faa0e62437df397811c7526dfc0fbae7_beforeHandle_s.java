class beforeHandle {
@Override
    protected int beforeHandle(Request request, Response response)
    {
        ComponentManager componentManager =
            (ComponentManager) getContext().getAttributes().get(Constants.XWIKI_COMPONENT_MANAGER);
        XWikiContext xwikiContext = Utils.getXWikiContext(componentManager);

        try {
            EntityReferenceSerializer<String> serializer =
                componentManager.getInstance(EntityReferenceSerializer.TYPE_STRING);

            /*
             * We add headers to the response to allow applications to verify if the authentication is still valid. We
             * are also adding the XWiki version at the same moment.
             */
            Series<Header> responseHeaders =
                (Series<Header>) response.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
            if (responseHeaders == null) {
                responseHeaders = new Series<>(Header.class);
                response.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, responseHeaders);
            }
            responseHeaders.add("XWiki-User", serializer.serialize(xwikiContext.getUserReference()));
            responseHeaders.add("XWiki-Version", xwikiContext.getWiki().getVersion());
        } catch (ComponentLookupException e) {
            getLogger()
                .warning("Failed to lookup the entity reference serializer: " + ExceptionUtils.getRootCauseMessage(e));
        }

        return CONTINUE;
    }
}
