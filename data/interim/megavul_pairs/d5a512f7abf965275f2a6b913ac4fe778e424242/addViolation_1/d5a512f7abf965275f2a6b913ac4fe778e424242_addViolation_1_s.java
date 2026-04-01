class addViolation_1 {
public void addViolation(String propertyName, String key, String message) {
        violationOccurred = true;
        String messageTemplate = escapeEl(message);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addPropertyNode(propertyName)
                .addBeanNode().inIterable().atKey(key)
                .addConstraintViolation();
    }
}
