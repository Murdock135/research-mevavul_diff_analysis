class render {
@Override
    public String render(XWikiContext context) throws XWikiException
    {
        CreateActionRequestHandler handler = new CreateActionRequestHandler(context);

        // Read the request and extract the passed information.
        handler.processRequest();

        // Save the determined values so we have them available in the action template.
        ScriptContext scontext = getCurrentScriptContext();
        scontext.setAttribute(SPACE_REFERENCE, handler.getSpaceReference(), ScriptContext.ENGINE_SCOPE);
        scontext.setAttribute(NAME, handler.getName(), ScriptContext.ENGINE_SCOPE);
        scontext.setAttribute(IS_SPACE, handler.isSpace(), ScriptContext.ENGINE_SCOPE);
        // put the available templates on the context, for the .vm to not compute them again
        scontext.setAttribute("availableTemplateProviders", handler.getAvailableTemplateProviders(),
            ScriptContext.ENGINE_SCOPE);
        scontext.setAttribute("recommendedTemplateProviders", handler.getRecommendedTemplateProviders(),
            ScriptContext.ENGINE_SCOPE);

        DocumentReference newDocumentReference = handler.getNewDocumentReference();
        if (newDocumentReference == null) {
            // There is information still missing, go back to the template and fill it.
            return CREATE_TEMPLATE;
        }

        // Check if the creation in the spaceReference is allowed.
        if (!handler.isTemplateProviderAllowedToCreateInCurrentSpace()) {
            // The selected template provider is not usable in the selected location. Go back to the template and pick
            // something else.
            return CREATE_TEMPLATE;
        }

        // Checking the rights to create the new document.
        // Note: Note checking the logical spaceReference, but the space of the final actual document reference, since
        // that is where we are creating the new document.
        checkRights(newDocumentReference.getLastSpaceReference(), context);

        // Check if the document to create already exists and if it respects the name strategy
        XWikiDocument newDocument = context.getWiki().getDocument(newDocumentReference, context);
        if (handler.isDocumentAlreadyExisting(newDocument) || handler.isDocumentPathTooLong(newDocumentReference)
            || !this.isEntityReferenceNameValid(newDocumentReference)) {
            return CREATE_TEMPLATE;
        }

        // Verify if the "type" of document to create has been set, even if we currently do not use it in the action.
        // The goal is let the user be able to chose it, which have some consequences in the UI (thanks to javascript).
        // See: https://jira.xwiki.org/browse/XWIKI-12580
        // Note: we do not need the "type" if we have a template provider: the type of the new document will be the type
        // of the template.
        // TODO: handle this type in doCreate() that we call above (see: https://jira.xwiki.org/browse/XWIKI-12585).
        if (StringUtils.isBlank(handler.getType()) && !handler.hasTemplate()) {
            return CREATE_TEMPLATE;
        }

        // create is finally valid, can be executed
        doCreate(context, newDocument, handler.isSpace(), handler.getTemplateProvider());

        return null;
    }
}
