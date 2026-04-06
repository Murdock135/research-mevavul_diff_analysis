class begin {
private void begin(FilterEventParameters parameters) throws FilterException
    {
        DocumentReference documentReference =
            this.documentEntityResolver.resolve(this.currentEntityReference, getDefaultDocumentReference());

        if (this.entity == null) {
            this.entity = new XWikiDocument(documentReference, this.currentLocale);
        } else {
            this.entity.setDocumentReference(documentReference);
            this.entity.setLocale(this.currentLocale);
        }

        // Mark the document as restricted to avoid that any scripts are executed as scripts should only be executed
        // on the current, saved version, see https://jira.xwiki.org/browse/XWIKI-20594
        this.entity.setRestricted(true);

        // Find default author
        DocumentReference defaultAuthorDocumentReference;
        // TODO: move to UserReference based APIs in DocumentInstanceOutputProperties
        if (this.properties.isAuthorSet()) {
            defaultAuthorDocumentReference = this.properties.getAuthor();
        } else {
            XWikiContext xcontext = xcontextProvider.get();
            defaultAuthorDocumentReference = xcontext != null ? xcontext.getUserReference() : null;
        }
        UserReference defaultAuthorReference = this.userDocumentResolver.resolve(defaultAuthorDocumentReference);

        this.entity
            .setCreationDate(getDate(WikiDocumentFilter.PARAMETER_CREATION_DATE, this.currentLocaleParameters, null));

        this.entity.getAuthors().setCreator(getUserReference(WikiDocumentFilter.PARAMETER_CREATION_AUTHOR,
            this.currentLocaleParameters, defaultAuthorReference));
        this.entity.setDefaultLocale(this.currentDefaultLocale);

        this.entity.setSyntax(getSyntax(WikiDocumentFilter.PARAMETER_SYNTAX, parameters, null));

        this.entity.setParentReference(getEntityReference(WikiDocumentFilter.PARAMETER_PARENT, parameters, null));
        this.entity.setCustomClass(getString(WikiDocumentFilter.PARAMETER_CUSTOMCLASS, parameters, null));
        this.entity.setTitle(getString(WikiDocumentFilter.PARAMETER_TITLE, parameters, null));
        this.entity.setDefaultTemplate(getString(WikiDocumentFilter.PARAMETER_DEFAULTTEMPLATE, parameters, null));
        this.entity.setValidationScript(getString(WikiDocumentFilter.PARAMETER_VALIDATIONSCRIPT, parameters, null));
        this.entity.setHidden(getBoolean(WikiDocumentFilter.PARAMETER_HIDDEN, parameters, false));

        this.entity.setMinorEdit(getBoolean(WikiDocumentFilter.PARAMETER_REVISION_MINOR, parameters, false));

        this.entity.getAuthors().setEffectiveMetadataAuthor(getUserReference(
            WikiDocumentFilter.PARAMETER_REVISION_EFFECTIVEMETADATA_AUTHOR, parameters, defaultAuthorReference));
        // Use effectuve metadata author as default as this value used to be used both both original and effective
        // metadata authors
        this.entity.getAuthors()
            .setOriginalMetadataAuthor(getUserReference(WikiDocumentFilter.PARAMETER_REVISION_ORIGINALMETADATA_AUTHOR,
                parameters, this.entity.getAuthors().getEffectiveMetadataAuthor()));

        this.entity.getAuthors().setContentAuthor(
            getUserReference(WikiDocumentFilter.PARAMETER_CONTENT_AUTHOR, parameters, defaultAuthorReference));

        String revisions =
            getString(XWikiWikiDocumentFilter.PARAMETER_JRCSREVISIONS, this.currentLocaleParameters, null);
        if (revisions != null) {
            try {
                this.entity.setDocumentArchive(revisions);
            } catch (XWikiException e) {
                throw new FilterException("Failed to set document archive", e);
            }
        }

        if (this.currentVersion != null && this.properties.isVersionPreserved()) {
            if (VALID_VERSION.matcher(this.currentVersion).matches()) {
                this.entity.setVersion(this.currentVersion);
            } else if (NumberUtils.isDigits(this.currentVersion)) {
                this.entity.setVersion(this.currentVersion + ".1");
            } else {
                // TODO: log something, probably a warning
            }
        }

        this.entity.setDate(getDate(WikiDocumentFilter.PARAMETER_REVISION_DATE, parameters, new Date()));
        this.entity.setComment(getString(WikiDocumentFilter.PARAMETER_REVISION_COMMENT, parameters, ""));

        this.entity.setContentUpdateDate(getDate(WikiDocumentFilter.PARAMETER_CONTENT_DATE, parameters, new Date()));

        // Content

        if (this.contentListener != null) {
            // Remember the current rendering context target syntax
            this.previousTargetSyntax = this.renderingContext.getTargetSyntax();
        }

        if (parameters.containsKey(WikiDocumentFilter.PARAMETER_CONTENT)) {
            this.entity.setContent(getString(WikiDocumentFilter.PARAMETER_CONTENT, parameters, null));

            if (this.contentListener != null) {
                // Cancel any existing content listener
                this.currentWikiPrinter = null;
                this.contentListener.setWrappedListener(null);
            }
        } else if (this.contentListener != null) {
            if (this.properties != null && this.properties.getDefaultSyntax() != null) {
                this.entity.setSyntax(this.properties.getDefaultSyntax());
            } else {
                // Make sure to set the default syntax if none were provided
                this.entity.setSyntax(this.entity.getSyntax());
            }

            ComponentManager componentManager = this.componentManagerProvider.get();

            String syntaxString = this.entity.getSyntax().toIdString();
            if (componentManager.hasComponent(PrintRendererFactory.class, syntaxString)) {
                PrintRendererFactory rendererFactory;
                try {
                    rendererFactory = componentManager.getInstance(PrintRendererFactory.class, syntaxString);
                } catch (ComponentLookupException e) {
                    throw new FilterException(
                        String.format("Failed to find PrintRendererFactory for syntax [%s]", this.entity.getSyntax()),
                        e);
                }

                this.currentWikiPrinter = new DefaultWikiPrinter();
                ((MutableRenderingContext) this.renderingContext).setTargetSyntax(rendererFactory.getSyntax());
                this.contentListener.setWrappedListener(rendererFactory.createRenderer(this.currentWikiPrinter));
            }
        }

        // Initialize the class
        getBaseClassOutputFilterStream().setEntity(this.entity.getXClass());
    }
}
