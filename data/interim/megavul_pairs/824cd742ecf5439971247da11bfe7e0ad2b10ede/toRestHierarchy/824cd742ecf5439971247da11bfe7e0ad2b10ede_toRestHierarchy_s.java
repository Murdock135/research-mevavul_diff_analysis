class toRestHierarchy {
public Hierarchy toRestHierarchy(EntityReference targetEntityReference, Boolean withPrettyNames)
    {
        XWikiContext xcontext = this.xcontextProvider.get();
        XWiki xwiki = xcontext.getWiki();
        Hierarchy hierarchy = new Hierarchy();
        for (EntityReference entityReference : targetEntityReference.getReversedReferenceChain()) {
            HierarchyItem hierarchyItem = new HierarchyItem();
            hierarchyItem.setName(entityReference.getName());
            hierarchyItem.setLabel(entityReference.getName());
            hierarchyItem.setType(entityReference.getType().getLowerCase());
            hierarchyItem.setUrl(xwiki.getURL(entityReference, xcontext));
            if (withPrettyNames) {
                try {
                    if (entityReference.getType() == EntityType.SPACE
                        || entityReference.getType() == EntityType.DOCUMENT) {
                        XWikiDocument document =
                            xwiki.getDocument(entityReference, xcontext).getTranslatedDocument(xcontext);
                        hierarchyItem.setLabel(document.getRenderedTitle(Syntax.PLAIN_1_0, xcontext));
                        hierarchyItem.setUrl(xwiki.getURL(document.getDocumentReferenceWithLocale(), xcontext));
                    } else if (entityReference.getType() == EntityType.WIKI) {
                        WikiDescriptor wikiDescriptor = this.wikiDescriptorManager.getById(entityReference.getName());
                        if (wikiDescriptor != null) {
                            hierarchyItem.setLabel(wikiDescriptor.getPrettyName());
                        }
                    }
                } catch (Exception e) {
                    this.logger.warn(
                        "Failed to get the pretty name of entity [{}]. Continue using the entity name. Root cause is [{}].",
                        entityReference, ExceptionUtils.getRootCauseMessage(e));
                }
            }
            hierarchy.withItems(hierarchyItem);
        }
        return hierarchy;
    }
}
