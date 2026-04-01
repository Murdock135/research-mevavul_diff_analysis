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
        errorMessage = String.format("Invalid integer value: '%s'", value);
      }

      if (!failed && longValue < 0) {
        failed = true;
        errorMessage = String.format("Expected positive integer value, got: '%s'", value);
      }

      if (!failed) {
        return true;
      }

      LOG.warn(errorMessage);
      context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();

      return false;
    }
}
