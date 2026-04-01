class validateConcreteScmMaterial {
@Override
    public void validateConcreteScmMaterial(ValidationContext validationContext) {
        if (getView() == null || getView().trim().isEmpty()) {
            errors.add(VIEW, "P4 view cannot be empty.");
        }
        if (StringUtils.isBlank(getServerAndPort())) {
            errors.add(SERVER_AND_PORT, "P4 port cannot be empty.");
        }

        if (!new UrlArgument(getUrl()).isValidURLOrLocalPath()) {
            errors.add(SERVER_AND_PORT, "P4 port seems to be invalid.");
        }

        view.validate(validationContext);
    }
}
