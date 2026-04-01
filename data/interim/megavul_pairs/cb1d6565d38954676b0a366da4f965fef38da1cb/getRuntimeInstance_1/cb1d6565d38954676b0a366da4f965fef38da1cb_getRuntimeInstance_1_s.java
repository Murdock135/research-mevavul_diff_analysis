class getRuntimeInstance_1 {
public <T extends JiffleRuntime> T getRuntimeInstance(Class<T> baseClass) throws
            it.geosolutions.jaiext.jiffle.JiffleException {
        RuntimeModel model = RuntimeModel.get(baseClass);
        if (model == null) {
            throw new it.geosolutions.jaiext.jiffle.JiffleException(baseClass.getName() +
                    " does not implement a required Jiffle runtime interface");
        }

        return (T) createRuntimeInstance(model, baseClass, false);
    }
}
