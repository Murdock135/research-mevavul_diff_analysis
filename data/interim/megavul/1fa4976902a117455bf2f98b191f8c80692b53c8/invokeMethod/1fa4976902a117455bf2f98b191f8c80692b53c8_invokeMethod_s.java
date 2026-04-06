class invokeMethod {
static void invokeMethod(Component instance, Class<?> clazz,
            String methodName, JsonArray args, int promiseId, boolean inert) {
        assert instance != null;
        Optional<Method> method = findMethod(instance, clazz, methodName);
        if (method.isPresent()) {
            invokeMethod(instance, method.get(), args, promiseId, inert);
        } else if (instance instanceof Composite) {
            Component compositeContent = ((Composite<?>) instance).getContent();
            invokeMethod(compositeContent, compositeContent.getClass(),
                    methodName, args, promiseId, inert);
        } else {
            String msg = String.format("Neither class '%s' "
                    + "nor its super classes declare event handler method '%s'",
                    instance.getClass().getName(), methodName);
            throw new IllegalStateException(msg);
        }
    }
}
