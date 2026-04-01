class addViolation {
public void addViolation(String propertyName, String message) {
        violationOccurred = true;
        String messageTemplate = escapeEl(message);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addPropertyNode(propertyName)
                .addConstraintViolation();
    }
}
