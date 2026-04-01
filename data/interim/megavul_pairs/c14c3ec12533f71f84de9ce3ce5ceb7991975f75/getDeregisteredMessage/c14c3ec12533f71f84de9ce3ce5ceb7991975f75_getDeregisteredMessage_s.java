class getDeregisteredMessage {
protected Message getDeregisteredMessage(Instance instance, StandardEvaluationContext context) {
		String activitySubtitle = evaluateExpression(context, deregisterActivitySubtitle);
		return createMessage(instance, deRegisteredTitle, activitySubtitle, context);
	}
}
