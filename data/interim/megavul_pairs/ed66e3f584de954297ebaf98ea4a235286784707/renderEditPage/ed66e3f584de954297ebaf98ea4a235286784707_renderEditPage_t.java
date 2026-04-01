class renderEditPage {
private void renderEditPage(RequestEvent event, FeatureModel featureModel) throws IOException {
        List<CSRFToken> tokens = new ArrayList<>();
        for (CSRFTokenProvider provider : Services.get(CSRFTokenProvider.class)) {
            CSRFToken token = provider.getToken(event.getRequest());
            if (token != null) {
                tokens.add(token);
            }
        }
        Map<String, Object> model = new HashMap<>();
        model.put("model", featureModel);
        model.put("tokens", tokens);

        String template = getResourceAsString("edit.html");
        String content = new Engine().transform(template, model);
        writeResponse(event, content);
    }
}
