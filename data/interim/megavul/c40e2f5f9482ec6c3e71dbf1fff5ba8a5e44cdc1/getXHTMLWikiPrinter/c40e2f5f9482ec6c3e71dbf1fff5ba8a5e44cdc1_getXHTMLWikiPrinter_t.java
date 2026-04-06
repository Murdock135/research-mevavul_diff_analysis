class getXHTMLWikiPrinter {
protected XHTMLWikiPrinter getXHTMLWikiPrinter()
    {
        if (this.xhtmlWikiPrinter == null) {
            this.xhtmlWikiPrinter = new XHTMLWikiPrinter(getPrinter(), getHtmlElementSanitizer());
        }
        return this.xhtmlWikiPrinter;
    }
}
