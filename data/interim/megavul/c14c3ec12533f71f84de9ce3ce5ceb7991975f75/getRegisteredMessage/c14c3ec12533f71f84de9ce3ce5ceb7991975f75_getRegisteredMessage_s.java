class getRegisteredMessage {
protected Message getRegisteredMessage(Instance instance, StandardEvaluationContext context) {
		String activitySubtitle = evaluateExpression(context, registerActivitySubtitle);
		return createMessage(instance, registeredTitle, activitySubtitle, context);
	}
}
