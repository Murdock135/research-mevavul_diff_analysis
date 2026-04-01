class transformResponse {
protected @Nullable String transformResponse(String response, String transformation) {
        String transformedResponse;

        try {
            String[] parts = splitTransformationConfig(transformation);
            String transformationType = parts[0];
            String transformationFunction = parts[1];

            TransformationService transformationService = TransformationHelper.getTransformationService(bundleContext,
                    transformationType);
            if (transformationService != null) {
                transformedResponse = transformationService.transform(transformationFunction, response);
            } else {
                transformedResponse = response;
                logger.warn("Couldn't transform response because transformationService of type '{}' is unavailable",
                        transformationType);
            }
        } catch (TransformationException te) {
            logger.warn("An exception occurred while transforming '{}' with '{}' : '{}'", response, transformation,
                    te.getMessage());

            // in case of an error we return the response without any transformation
            transformedResponse = response;
        }

        logger.debug("Transformed response is '{}'", transformedResponse);
        return transformedResponse;
    }
}
