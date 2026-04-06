class getStatusChangedMessage {
protected Message getStatusChangedMessage(Instance instance, EvaluationContext context) {
		String activitySubtitle = evaluateExpression(context, statusActivitySubtitle);
		return createMessage(instance, statusChangedTitle, activitySubtitle, context);
	}
}
