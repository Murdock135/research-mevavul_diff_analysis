class evaluate {
@Override
    public String evaluate(String script, ScriptContext context) {
        Expression expression = spelExpressionParser.parseExpression(script);
        SimpleEvaluationContext spelContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        return expression.getValue(spelContext, String.class);
    }
}
