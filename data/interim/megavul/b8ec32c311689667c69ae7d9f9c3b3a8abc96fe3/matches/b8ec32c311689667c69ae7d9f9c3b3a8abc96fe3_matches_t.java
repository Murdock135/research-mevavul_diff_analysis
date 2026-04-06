class matches {
@Override
    public boolean matches(ConditionContext context) {
        BeanContext beanContext = context.getBeanContext();
        if (beanContext instanceof ApplicationContext) {
            List<String> paths = ((ApplicationContext) beanContext)
                    .getEnvironment()
                    .getProperty(FileWatchConfiguration.PATHS, ConversionContext.LIST_OF_STRING)
                    .orElse(null);

            if (CollectionUtils.isNotEmpty(paths)) {


                boolean matchedPaths = paths.stream().anyMatch(p -> new File(p).exists());
                if (!matchedPaths) {
                    context.fail("File watch disabled because no paths matching the watch pattern exist (Paths: " + paths + ")");
                }
                return matchedPaths;
            }
        }

        context.fail("File watch disabled because no watch paths specified");
        return false;
    }
}
