class setDisabledStatus {
public void setDisabledStatus(boolean disabledStatus)
    {
        if (hasAdminRights()) {
            this.user.setDisabled(disabledStatus, getXWikiContext());
        }
    }
}
