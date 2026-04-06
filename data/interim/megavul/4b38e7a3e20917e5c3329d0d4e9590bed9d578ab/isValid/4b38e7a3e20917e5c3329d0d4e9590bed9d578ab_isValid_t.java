class isValid {
@Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
      if (value != null && StringUtils.isNotEmpty(String.valueOf(value))) {
        return true;
      }

      String escapedValue = MessageSanitizer.escape(value == null ? null : value.toString());
      String errorMessage = String.format("Expected not empty value, got '%s'", escapedValue);
      LOG.warn(errorMessage);

      context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
      return false;
    }
}
