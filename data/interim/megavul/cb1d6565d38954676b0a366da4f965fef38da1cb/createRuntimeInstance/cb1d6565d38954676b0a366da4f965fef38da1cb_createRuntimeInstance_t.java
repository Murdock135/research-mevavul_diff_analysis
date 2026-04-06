class createRuntimeInstance {
private JiffleRuntime createRuntimeInstance(RuntimeModel model, Class<? extends JiffleRuntime> runtimeClass, boolean scriptInDocs) throws
            
            it.geosolutions.jaiext.jiffle.JiffleException {
        if (!isCompiled()) {
            throw new it.geosolutions.jaiext.jiffle.JiffleException("The script has not been compiled");
        }

        String runtimeSource = createRuntimeSource(model, runtimeClass.getName(), scriptInDocs);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Jiffle script compiled to:\n\n" + runtimeSource);    
        }
        try {
            SimpleCompiler compiler = new SimpleCompiler();
            compiler.cook(runtimeSource);

            StringBuilder sb = new StringBuilder();
            sb.append(it.geosolutions.jaiext.jiffle.JiffleProperties.get(
                    it.geosolutions.jaiext.jiffle.JiffleProperties.RUNTIME_PACKAGE_KEY)).append(".");

            switch (model) {
                case DIRECT:
                    sb.append(it.geosolutions.jaiext.jiffle.JiffleProperties.get(
                            it.geosolutions.jaiext.jiffle.JiffleProperties.DIRECT_CLASS_KEY));
                    break;

                case INDIRECT:
                    sb.append(it.geosolutions.jaiext.jiffle.JiffleProperties.get(
                            it.geosolutions.jaiext.jiffle.JiffleProperties.INDIRECT_CLASS_KEY));
                    break;

                default:
                    throw new IllegalArgumentException("Internal compiler error");
            }

            Class<?> clazz = compiler.getClassLoader().loadClass(sb.toString());
            JiffleRuntime runtime = (JiffleRuntime) clazz.newInstance();
            runtime.setImageParams(imageParams);
            if (runtime instanceof JiffleIndirectRuntime) {
                ((JiffleIndirectRuntime) runtime).setDestinationBands(destinationBands);
            }
            return runtime;

        } catch (Exception ex) {
            // do not display the source code in indirect runtime exception messages
            if (model == RuntimeModel.INDIRECT) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Runtime source error for source: " + runtimeSource);
                }
                throw new JiffleException("Runtime source error", ex);
            }
            throw new JiffleException("Runtime source error for source: " + runtimeSource, ex);
        }
    }
}
