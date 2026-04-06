class htmlBody_1 {
@Override
    protected HtmlRenderable htmlBody() {
        return HtmlElement.li().content(
            HtmlElement.span(HtmlAttribute.cssClass("artifact")).content(
                HtmlElement.a(HtmlAttribute.href(getUrl()))
                        .safecontent(getFileName())
            )
        );

    }
}
