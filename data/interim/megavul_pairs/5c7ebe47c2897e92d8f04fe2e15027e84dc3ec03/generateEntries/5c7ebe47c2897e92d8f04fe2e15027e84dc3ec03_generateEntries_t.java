class generateEntries {
private void generateEntries(Block parentBlock, SyndFeed feed, RssMacroParameters parameters)
        throws MacroExecutionException
    {
        int maxElements = parameters.getCount();
        int count = 0;

        for (Object item : feed.getEntries()) {
            ++count;
            if (count > maxElements) {
                break;
            }
            SyndEntry entry = (SyndEntry) item;

            ResourceReference titleResourceReference = new ResourceReference(entry.getLink(), ResourceType.URL);
            Block titleBlock = new LinkBlock(parsePlainText(entry.getTitle()), titleResourceReference, true);
            ParagraphBlock paragraphTitleBlock = new ParagraphBlock(Collections.singletonList(titleBlock));
            paragraphTitleBlock.setParameter(CLASS_ATTRIBUTE, "rssitemtitle");
            parentBlock.addChild(paragraphTitleBlock);

            if (parameters.isContent() && entry.getDescription() != null) {
                // We are wrapping the feed entry content in a HTML macro, not considering what the declared content
                // is, because some feed will declare text while they actually contain HTML.
                // See http://stuffthathappens.com/blog/2007/10/29/i-hate-rss/
                // A case where doing this might hurt is if a feed declares "text" and has any XML inside it does
                // not want to be interpreted as such, but displayed as is instead. But this certainly is too rare
                // compared to mis-formed feeds that say text while they want to say HTML.
                Block html = new RawBlock(cleanHTML(entry.getDescription().getValue()), Syntax.HTML_5_0);
                parentBlock.addChild(new GroupBlock(Arrays.asList(html), Collections.singletonMap(CLASS_ATTRIBUTE,
                    "rssitemdescription")));
            }
        }
    }
}
