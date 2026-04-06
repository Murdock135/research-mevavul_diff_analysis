class getRevision_1 {
@Override
    public XWikiDocument getRevision(DocumentReference reference, String revision) throws XWikiException
    {
        Pair<String, String> parsedRevision = parseRevision(revision);

        // Load the document revision
        return getProvider(parsedRevision.getLeft()).getRevision(reference, parsedRevision.getRight());
    }
}
