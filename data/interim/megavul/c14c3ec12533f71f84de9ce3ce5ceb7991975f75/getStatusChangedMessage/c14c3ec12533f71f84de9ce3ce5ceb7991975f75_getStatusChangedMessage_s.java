class getStatusChangedMessage {
protected Message getStatusChangedMessage(Instance instance, StandardEvaluationContext context) {
		String activitySubtitle = evaluateExpression(context, statusActivitySubtitle);
		return createMessage(instance, statusChangedTitle, activitySubtitle, context);
	}
}
