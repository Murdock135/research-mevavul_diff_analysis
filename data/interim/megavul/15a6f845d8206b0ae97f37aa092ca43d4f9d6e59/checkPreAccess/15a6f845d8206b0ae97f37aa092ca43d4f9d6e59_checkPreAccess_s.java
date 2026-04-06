class checkPreAccess {
private boolean checkPreAccess(Right right)
    {
        if (CONTENT_AUTHOR_RIGHTS.contains(right)) {
            if (this.renderingContext.isRestricted()) {
                return false;
            } else if (right == Right.PROGRAM && this.xcontextProvider.get().hasDroppedPermissions()) {
                return false;
            }
        }

        return true;
    }
}
