class getRegisteredMessage {
protected Message getRegisteredMessage(Instance instance, EvaluationContext context) {
		String activitySubtitle = evaluateExpression(context, registerActivitySubtitle);
		return createMessage(instance, registeredTitle, activitySubtitle, context);
	}
}
