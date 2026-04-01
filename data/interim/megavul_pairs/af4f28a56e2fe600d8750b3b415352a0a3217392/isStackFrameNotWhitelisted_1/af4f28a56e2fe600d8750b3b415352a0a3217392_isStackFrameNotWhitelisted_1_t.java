class isStackFrameNotWhitelisted_1 {
private boolean isStackFrameNotWhitelisted(StackFrame sf) {
		return isCallNotWhitelisted(sf.getClassName(), sf.getMethodName());
	}
}
