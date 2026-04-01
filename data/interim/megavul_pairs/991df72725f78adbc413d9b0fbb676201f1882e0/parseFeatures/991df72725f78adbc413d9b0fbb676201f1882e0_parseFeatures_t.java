class parseFeatures {
private void parseFeatures(FF4jConfiguration ff4jConfig, Map<String, String> mapConf) {
        int idx = 0;
        String currentFeatureKey = FF4J_TAG + "." + FEATURES_TAG  + "." + idx;
        while (mapConf.containsKey(currentFeatureKey +  "." + FEATURE_ATT_UID)) {
            assertKeyNotEmpty(mapConf, currentFeatureKey +  "." + FEATURE_ATT_UID);
            Feature f = new Feature(mapConf.get(currentFeatureKey +  "." + FEATURE_ATT_UID));
            // Enabled
            assertKeyNotEmpty(mapConf, currentFeatureKey +  "." + FEATURE_ATT_ENABLE);
            f.setEnable(Boolean.valueOf(mapConf.get(currentFeatureKey +  "." + FEATURE_ATT_ENABLE)));
            // Description
            String description = mapConf.get(currentFeatureKey +  "." + FEATURE_ATT_DESC);
            if (null != description && !"".equals(description)) {
                f.setDescription(description);
            }
            // Group
            String groupName = mapConf.get(currentFeatureKey +  "." + FEATURE_ATT_GROUP);
            if (null != groupName && !"".equals(groupName)) {
                f.setGroup(groupName);
            }
            // Permissions
            String strPermissions = mapConf.get(currentFeatureKey +  "." + FEATURE_ATT_PERMISSIONS);
            if (null != strPermissions && !"".equals(strPermissions)) {
                f.setPermissions(
                        Arrays.asList(strPermissions.split(","))
                              .stream()
                              .map(String::trim)
                              .collect(Collectors.toSet()));
            }
            // Custom Properties
            f.setCustomProperties(parseProperties(currentFeatureKey + "." + FEATURE_ATT_PROPERTIES, mapConf));
            // FlipStrategy
            String flipStrategyClass = mapConf.get(currentFeatureKey +  "." + TOGGLE_STRATEGY_TAG + "." + TOGGLE_STRATEGY_ATTCLASS);
            if (null != flipStrategyClass && !"".equals(flipStrategyClass)) {
                FlippingStrategy flipStrategy = null;
                try {
                    Class<?> typeClass = Class.forName(flipStrategyClass);
                    if (!FlippingStrategy.class.isAssignableFrom(typeClass)) {
                        throw new IllegalArgumentException("Cannot create flipstrategy <" + flipStrategyClass + "> invalid type");
                    }
                    flipStrategy = (FlippingStrategy) typeClass.newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot parse flipStrategy for feature '" + f.getUid() + 
                            "' -> check key [" + currentFeatureKey +  "." + TOGGLE_STRATEGY_TAG + "." + TOGGLE_STRATEGY_ATTCLASS + "]", e);
                }
                int idxParam = 0;
                String currentParamKey = currentFeatureKey +  "." + TOGGLE_STRATEGY_TAG + "." + TOGGLE_STRATEGY_PARAMTAG + "." + idxParam;
                Map<String, String> params = new HashMap<>();
                while (mapConf.containsKey(currentParamKey+  "." + TOGGLE_STRATEGY_PARAMNAME)) {
                    assertKeyNotEmpty(mapConf, currentParamKey + "." + TOGGLE_STRATEGY_PARAMNAME);
                    assertKeyNotEmpty(mapConf, currentParamKey + "." + TOGGLE_STRATEGY_PARAMVALUE);
                    params.put(mapConf.get(currentParamKey + "." + TOGGLE_STRATEGY_PARAMNAME), 
                               mapConf.get(currentParamKey + "." + TOGGLE_STRATEGY_PARAMVALUE));
                    currentParamKey = currentFeatureKey +  "." + TOGGLE_STRATEGY_TAG + "." + TOGGLE_STRATEGY_PARAMTAG + "." + ++idxParam;
                }
                flipStrategy.init(f.getUid(), params);
                f.setFlippingStrategy(flipStrategy);
                
            }
            ff4jConfig.getFeatures().put(f.getUid(), f);
            
            // ff4j.features.X
            currentFeatureKey = FF4J_TAG + "." + FEATURES_TAG  + "." + ++idx;
        }
        
    }
}
