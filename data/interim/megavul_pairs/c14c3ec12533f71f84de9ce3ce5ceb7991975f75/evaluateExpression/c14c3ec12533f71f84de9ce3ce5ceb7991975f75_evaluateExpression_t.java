class evaluateExpression {
protected String evaluateExpression(EvaluationContext context, Expression expression) {
		return Objects.requireNonNull(expression.getValue(context, String.class));
	}
}
