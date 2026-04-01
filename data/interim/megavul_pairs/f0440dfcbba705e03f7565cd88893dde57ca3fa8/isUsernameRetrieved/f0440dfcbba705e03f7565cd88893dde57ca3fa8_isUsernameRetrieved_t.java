class isUsernameRetrieved {
@Deprecated
    public boolean isUsernameRetrieved(String username)
    {
        try {
            getDriver()
                .findElementWithoutWaiting(By.xpath("//div[@id='xwikicontent']//strong[text()='" + username + "']"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
