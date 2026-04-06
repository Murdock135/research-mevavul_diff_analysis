class setDisabledStatus {
public void setDisabledStatus(boolean disabledStatus)
    {
        this.user.setDisabled(disabledStatus, getXWikiContext());
    }
}
