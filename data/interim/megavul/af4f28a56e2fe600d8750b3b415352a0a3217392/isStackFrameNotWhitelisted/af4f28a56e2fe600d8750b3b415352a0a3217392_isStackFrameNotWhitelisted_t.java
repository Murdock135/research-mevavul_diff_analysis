class isStackFrameNotWhitelisted {
private boolean isStackFrameNotWhitelisted(StackTraceElement ste) {
		return isCallNotWhitelisted(ste.getClassName(), ste.getMethodName());
	}
}
