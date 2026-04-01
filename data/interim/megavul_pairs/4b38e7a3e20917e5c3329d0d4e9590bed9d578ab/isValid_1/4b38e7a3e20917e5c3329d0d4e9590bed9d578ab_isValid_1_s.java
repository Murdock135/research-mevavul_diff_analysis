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
        String errorMessage = String.format("URL parameter '%s' is not a valid regexp", value);
        LOG.warn(errorMessage);

        context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
      }
      return false;
    }
}
