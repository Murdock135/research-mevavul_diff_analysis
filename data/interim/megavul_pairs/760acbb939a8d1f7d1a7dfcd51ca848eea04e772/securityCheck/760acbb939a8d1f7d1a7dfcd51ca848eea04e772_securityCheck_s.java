class securityCheck {
private void securityCheck(String filename) {
        Assert.doesNotContain(filename, "..");
    }
}
