class isValid_3 {
@Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
      if (proxyManager.get(value) != null) {
        return true;
      }

      String escapedValue = MessageSanitizer.escape(value.toString());
      String errorMessage = String.format("No proxy server found for specified port %s", escapedValue);
      LOG.warn(errorMessage);

      context.buildConstraintViolationWithTemplate(errorMessage).addPropertyNode(PARAM_NAME).addConstraintViolation();
      return false;
    }
}
