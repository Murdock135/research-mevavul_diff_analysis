class htmlBody {
@Override
    protected HtmlRenderable htmlBody() {
        return sequence(
                HtmlElement.div(cssClass("dir-container")).content(
                    HtmlElement.span(cssClass("directory")).content(
                        HtmlElement.a(onclick("BuildDetail.tree_navigator(this)"))
                                .content(getFileName())
                    )
                ),
                HtmlElement.div(cssClass("subdir-container"), style("display:none"))
                        .content(subDirectory)
        );
    }
}
