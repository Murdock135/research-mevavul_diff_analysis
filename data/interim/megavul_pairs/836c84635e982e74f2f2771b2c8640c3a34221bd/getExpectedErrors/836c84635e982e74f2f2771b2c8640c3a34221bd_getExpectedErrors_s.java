class getExpectedErrors {
@Override
	protected int getExpectedErrors() {
		return hasJavaNetURLPermission ? 2 : 1; // Security error must be reported as an error. Java 8 reports two errors.
	}
}
