class addViolation_3 {
public void addViolation(String message) {
        violationOccurred = true;
        String messageTemplate = escapeEl(message);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation();
    }
}
