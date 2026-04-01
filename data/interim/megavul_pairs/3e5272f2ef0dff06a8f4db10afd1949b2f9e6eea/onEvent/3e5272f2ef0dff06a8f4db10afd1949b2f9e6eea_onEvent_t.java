class onEvent {
@Override
    public void onEvent(Event event, Object source, Object data)
    {
        try {
            if (event instanceof DocumentUpdatedEvent) {
                XWikiDocument document = (XWikiDocument) source;
                if (Locale.ROOT.equals(document.getLocale())) {
                    // Index all the translations of a document when its default translation has been updated because
                    // the default translation holds meta data shared by all translations (attachments, objects).
                    indexTranslations(document, (XWikiContext) data);
                } else {
                    // Index only the updated translation.
                    this.solrIndexer.get().index(document.getDocumentReferenceWithLocale(), false);
                }
            } else if (event instanceof DocumentCreatedEvent) {
                XWikiDocument document = (XWikiDocument) source;

                if (!Locale.ROOT.equals(document.getLocale())) {
                    // If a new translation is added to a document reindex the whole document (could be optimized a bit
                    // by reindexing only the parent locales but that would always include objects and attachments
                    // anyway)
                    indexTranslations(document, (XWikiContext) data);
                } else {
                    this.solrIndexer.get().index(document.getDocumentReferenceWithLocale(), false);
                }
            } else if (event instanceof DocumentDeletedEvent) {
                XWikiDocument document = ((XWikiDocument) source).getOriginalDocument();

                // We must pass the document reference with the REAL locale because when the indexer is going to delete
                // the document from the Solr index (later, on a different thread) the real locale won't be accessible
                // anymore since the XWiki document has been already deleted from the database. The real locale (taken
                // from the XWiki document) is used to compute the id of the Solr document when the document reference
                // locale is ROOT (i.e. for default document translations).
                // Otherwise the document won't be deleted from the Solr index (because the computed id won't match any
                // document from the Solr index) and we're going to have deleted documents that are still in the Solr
                // index. These documents will be filtered from the search results but not from the facet counts.
                // See XWIKI-10003: Cache problem with Solr facet filter results count
                this.solrIndexer.get().delete(
                    new DocumentReference(document.getDocumentReference(), document.getRealLocale()), false);
            } else if (event instanceof AttachmentUpdatedEvent || event instanceof AttachmentAddedEvent) {
                XWikiDocument document = (XWikiDocument) source;
                String fileName = ((AbstractAttachmentEvent) event).getName();
                XWikiAttachment attachment = document.getAttachment(fileName);

                this.solrIndexer.get().index(attachment.getReference(), false);
            } else if (event instanceof AttachmentDeletedEvent) {
                XWikiDocument document = ((XWikiDocument) source).getOriginalDocument();
                String fileName = ((AbstractAttachmentEvent) event).getName();
                XWikiAttachment attachment = document.getAttachment(fileName);

                this.solrIndexer.get().delete(attachment.getReference(), false);
            } else if (event instanceof XObjectUpdatedEvent || event instanceof XObjectAddedEvent) {
                EntityEvent entityEvent = (EntityEvent) event;

                this.solrIndexer.get().index(entityEvent.getReference(), false);
            } else if (event instanceof XObjectDeletedEvent) {
                EntityEvent entityEvent = (EntityEvent) event;

                this.solrIndexer.get().delete(entityEvent.getReference(), false);
            } else if (event instanceof XObjectPropertyUpdatedEvent || event instanceof XObjectPropertyAddedEvent) {
                EntityEvent entityEvent = (EntityEvent) event;

                this.solrIndexer.get().index(entityEvent.getReference(), false);
            } else if (event instanceof XObjectPropertyDeletedEvent) {
                EntityEvent entityEvent = (EntityEvent) event;

                this.solrIndexer.get().delete(entityEvent.getReference(), false);
            } else if (event instanceof WikiDeletedEvent) {
                String wikiName = (String) source;
                WikiReference wikiReference = new WikiReference(wikiName);

                this.solrIndexer.get().delete(wikiReference, false);
            } else if (event instanceof GeneralMailConfigurationUpdatedEvent) {
                // Refresh the index when the mail configuration is changed because the mail configuration is used to
                // decide if emails shall be indexed or not.
                if (source instanceof String) {
                    this.solrIndexer.get().index(new WikiReference((String) source), true);
                } else {
                    this.solrIndexer.get().index(null, true);
                }
            }
        } catch (Exception e) {
            this.logger.error("Failed to handle event [{}] with source [{}]", event, source.toString(), e);
        }
    }
}
