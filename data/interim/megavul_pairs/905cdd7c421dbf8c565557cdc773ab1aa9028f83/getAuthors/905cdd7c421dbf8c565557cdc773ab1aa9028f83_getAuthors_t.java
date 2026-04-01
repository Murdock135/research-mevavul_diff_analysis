class getAuthors {
@Unstable
    public DocumentAuthors getAuthors()
    {
        if (this.hasAccess(Right.PROGRAM)) {
            // We're using getDoc here to ensure to have a cloned doc
            return getDoc().getAuthors();
        } else {
            // in this case we don't care if the doc is cloned or not since it's readonly
            return new SafeDocumentAuthors(this.doc.getAuthors());
        }
    }
}
