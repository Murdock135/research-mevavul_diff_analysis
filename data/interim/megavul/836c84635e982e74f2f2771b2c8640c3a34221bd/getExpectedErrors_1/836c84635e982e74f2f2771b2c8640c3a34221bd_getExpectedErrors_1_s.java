class getExpectedErrors_1 {
@Override
	protected int getExpectedErrors() {
		return hasJavaNetURLPermission ? 3 : 2; // Security error must be reported as an error
	}
}
