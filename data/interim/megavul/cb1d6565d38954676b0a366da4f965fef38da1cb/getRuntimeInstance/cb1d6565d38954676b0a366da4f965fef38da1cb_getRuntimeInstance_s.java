class getRuntimeInstance {
public JiffleRuntime getRuntimeInstance(Jiffle.RuntimeModel model) throws
            it.geosolutions.jaiext.jiffle.JiffleException {
        return createRuntimeInstance(model, getRuntimeBaseClass(model), false);
    }
}
