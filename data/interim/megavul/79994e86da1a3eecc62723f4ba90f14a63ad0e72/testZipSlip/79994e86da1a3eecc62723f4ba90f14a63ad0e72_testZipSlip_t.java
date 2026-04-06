class testZipSlip {
@Test(expectedExceptions=IllegalArgumentException.class)
    public void testZipSlip() throws IOException {
    	File tempDir = TestUtils.createTempDirectory("openrefine-zip-slip-test");
        // For CVE-2018-19859, issue #1840
    	ImportingUtilities.allocateFile(tempDir, "../../tmp/script.sh");
    }
}
