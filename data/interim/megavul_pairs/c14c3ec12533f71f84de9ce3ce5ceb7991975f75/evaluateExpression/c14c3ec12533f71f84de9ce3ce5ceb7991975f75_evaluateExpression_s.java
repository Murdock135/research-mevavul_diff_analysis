class evaluateExpression {
protected String evaluateExpression(StandardEvaluationContext context, Expression expression) {
		return Objects.requireNonNull(expression.getValue(context, String.class));
	}
}
