class process {
@Override
    public void process(RequestEvent event) throws IOException {
        FeatureManager featureManager = event.getFeatureManager();
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();
		if(!validateCSRFToken(event)) {
			renderErrorPage(event);
			return;
		}
        // identify the feature
        Feature feature = null;
        String featureAsString = request.getParameter("f");
        for (Feature f : featureManager.getFeatures()) {
            if (f.name().equals(featureAsString)) {
                feature = f;
            }
        }
        if (feature == null) {
            response.sendError(400);
            return;
        }

        FeatureMetaData metadata = featureManager.getMetaData(feature);
        List<ActivationStrategy> impls = featureManager.getActivationStrategies();
        FeatureModel featureModel = new FeatureModel(feature, metadata, impls);

        // GET requests for this feature
        if ("GET".equals(request.getMethod())) {

            FeatureState state = featureManager.getFeatureState(feature);
            featureModel.populateFromFeatureState(state);

            renderEditPage(event, featureModel);

        }

        // POST requests for this feature
        if ("POST".equals(request.getMethod())) {

            featureModel.restoreFromRequest(request);

            // no validation errors
            if (featureModel.isValid()) {

                FeatureState state = featureModel.toFeatureState();
                featureManager.setFeatureState(state);
                response.sendRedirect("index");

            }
            // got validation errors
            else {
                renderEditPage(event, featureModel);
            }

        }

    }
}
