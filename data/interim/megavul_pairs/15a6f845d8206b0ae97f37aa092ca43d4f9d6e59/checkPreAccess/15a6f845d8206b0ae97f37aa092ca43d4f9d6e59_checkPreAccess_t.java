class checkPreAccess {
private boolean checkPreAccess(Right right)
    {
        if (CONTENT_AUTHOR_RIGHTS.contains(right)) {
            XWikiDocument doc = getProgrammingDocument();
            boolean restricted = this.renderingContext.isRestricted() || (doc != null && doc.isRestricted());
            return !(restricted || (right == Right.PROGRAM && this.xcontextProvider.get().hasDroppedPermissions()));
        }

        return true;
    }
}
