class parseFlipStrategy {
@SuppressWarnings("unchecked")
    private FlippingStrategy parseFlipStrategy(Feature feature, Map<String, Object> toggleStrategy) {
        try {
            // Parse class
            String clazzName = (String) toggleStrategy.get(TOGGLE_STRATEGY_ATTCLASS);
            FlippingStrategy flipStrategy = (FlippingStrategy) Class.forName(clazzName).newInstance();
            // Parse Params
            List<Map<String, Object>> mapYamlParam = (List<Map<String, Object>>) toggleStrategy.get(TOGGLE_STRATEGY_PARAMTAG);
            Map<String,String> params = new HashMap<>();
            for (Map<String, Object> currentParam : mapYamlParam) {
                params.put(
                        currentParam.get(TOGGLE_STRATEGY_PARAMNAME).toString(), 
                        currentParam.get(TOGGLE_STRATEGY_PARAMVALUE).toString());
            }
            flipStrategy.init(feature.getUid(), params);
            return flipStrategy;
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occurs during flipstrategy parsing TAG" + feature.getUid(), e);
        }
    }
}
