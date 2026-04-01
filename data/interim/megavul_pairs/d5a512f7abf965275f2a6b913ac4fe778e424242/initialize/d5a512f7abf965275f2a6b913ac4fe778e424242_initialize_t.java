class initialize {
@Override
    public void initialize(SelfValidating constraintAnnotation) {
        escapeExpressions = constraintAnnotation.escapeExpressions();
    }
}
