class addViolation_2 {
public void addViolation(String propertyName, Integer index, String message) {
        violationOccurred = true;
        String messageTemplate = escapeEl(message);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addPropertyNode(propertyName)
                .addBeanNode().inIterable().atIndex(index)
                .addConstraintViolation();
    }
}
