class cleanParametersFromMetadata {
private static WikiParameters cleanParametersFromMetadata(WikiParameters parameters)
    {
        WikiParameters wikiParameters = new WikiParameters();

        for (WikiParameter parameter : parameters) {
            boolean acceptParameter = !(parameter.getKey().startsWith(METADATA_ATTRIBUTE_PREFIX)
                || (
                parameter.getKey().equals(CLASS_ATTRIBUTE) && parameter.getValue().equals(METADATA_CONTAINER_CLASS)
            ));
            if (acceptParameter) {
                wikiParameters = wikiParameters.addParameter(parameter.getKey(), parameter.getValue());
            }
        }

        return wikiParameters;
    }
}
