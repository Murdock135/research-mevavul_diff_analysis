class canUndelete {
public boolean canUndelete()
    {
        try {
            return hasAccessLevel(ADMIN_RIGHT, getFullName()) || hasAccessLevel("undelete", getFullName())
                || (Objects.equals(this.context.getUserReference(), getDeleterReference())
                    && hasAccess(Right.EDIT, getDocumentReference()));
        } catch (XWikiException ex) {
            // Public APIs should not throw exceptions
            LOGGER.warn("Exception while checking if entry [{}] can be restored from the recycle bin", getId(), ex);
            return false;
        }
    }
}
