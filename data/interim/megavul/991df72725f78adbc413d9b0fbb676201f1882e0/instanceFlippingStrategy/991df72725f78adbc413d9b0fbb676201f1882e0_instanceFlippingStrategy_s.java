class instanceFlippingStrategy {
@SuppressWarnings("unchecked")
	public static FlippingStrategy instanceFlippingStrategy(String uid, String className,  Map<String, String> initparams) {
        try {
            Class<FlippingStrategy> clazz = (Class<FlippingStrategy>) (classLoader == null ? Class.forName(className) : classLoader.loadClass(className));
            FlippingStrategy flipStrategy = clazz.newInstance();
            flipStrategy.init(uid, initparams);
            return flipStrategy;
        } catch (Exception ie) {
            throw new FeatureAccessException("Cannot instantiate Strategy, no default constructor available", ie);
        } 
    }
}
