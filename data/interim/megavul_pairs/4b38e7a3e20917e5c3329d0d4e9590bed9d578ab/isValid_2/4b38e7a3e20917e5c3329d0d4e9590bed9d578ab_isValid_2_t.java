class isValid_2 {
@Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      long longValue = 0;
      boolean failed = false;
      String errorMessage = "";
      try {
        longValue = Long.parseLong(value);
      } catch (NumberFormatException ex) {
        failed = true;
        String escapedValue = MessageSanitizer.escape(value);
        errorMessage = String.format("Invalid integer value: '%s'", escapedValue);
      }

      if (!failed && longValue < 0) {
        failed = true;
        String escapedValue = MessageSanitizer.escape(value);
        errorMessage = String.format("Expected positive integer value, got: '%s'", escapedValue);
      }

      if (!failed) {
        return true;
      }

      LOG.warn(errorMessage);
      context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();

      return false;
    }
}
