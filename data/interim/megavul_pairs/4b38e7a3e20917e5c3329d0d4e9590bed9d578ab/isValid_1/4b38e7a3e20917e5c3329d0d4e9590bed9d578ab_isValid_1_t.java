class isValid_1 {
@Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (StringUtils.isEmpty(value)) {
        return true;
      }

      try {
        Pattern.compile(value);
        return true;
      } catch (Exception ex) {
        String escapedValue = MessageSanitizer.escape(value);
        String errorMessage = String.format("URL parameter '%s' is not a valid regexp", escapedValue);
        LOG.warn(errorMessage);

        context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
      }
      return false;
    }
}
