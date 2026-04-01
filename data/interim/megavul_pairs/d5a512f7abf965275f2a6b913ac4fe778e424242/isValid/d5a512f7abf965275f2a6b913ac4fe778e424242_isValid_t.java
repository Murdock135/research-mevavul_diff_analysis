class isValid {
@SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        final ViolationCollector collector = new ViolationCollector(context, escapeExpressions);
        context.disableDefaultConstraintViolation();
        for (ValidationCaller caller : methodMap.computeIfAbsent(value.getClass(), this::findMethods)) {
            caller.setValidationObject(value);
            caller.call(collector);
        }
        return !collector.hasViolationOccurred();
    }
}
