class evaluate {
@Override
    public String evaluate(String script, ScriptContext context) {
        Expression expression = spelExpressionParser.parseExpression(script);
        StandardEvaluationContext spelContext = new StandardEvaluationContext(context);
        return expression.getValue(spelContext, String.class);
    }
}
