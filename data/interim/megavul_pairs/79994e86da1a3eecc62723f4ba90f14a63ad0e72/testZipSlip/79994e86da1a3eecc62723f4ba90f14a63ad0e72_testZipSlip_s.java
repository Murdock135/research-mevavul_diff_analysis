class testZipSlip {
@Test(expectedExceptions=IllegalArgumentException.class)
    public void testZipSlip() {
        // For CVE-2018-19859, issue #1840
    	ImportingUtilities.allocateFile(workspaceDir, "../../script.sh");
    }
}
