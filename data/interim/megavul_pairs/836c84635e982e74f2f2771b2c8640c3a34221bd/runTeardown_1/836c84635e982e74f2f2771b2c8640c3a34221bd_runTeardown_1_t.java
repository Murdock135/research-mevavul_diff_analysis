class runTeardown_1 {
@Override
	protected void runTeardown() {
		Assert.assertTrue("Error during initialization", messagedInitialization);
		Assert.assertTrue("Socket connection is not allowed", securityExceptionOccurred);
	}
}
