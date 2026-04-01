class findMethod {
private static Optional<Method> findMethod(Component instance,
            Class<?> clazz, String methodName) {
        List<Method> methods = Stream.of(clazz.getDeclaredMethods())
                .filter(method -> methodName.equals(method.getName()))
                .filter(method -> hasMethodAnnotation(method))
                .collect(Collectors.toList());
        if (methods.size() > 1) {
            getLogger().error(String.format(
                    "Method conflict in event handler. Class '%s' contains "
                            + "several event handler methods with the same name '%s'",
                    instance.getClass().getName(), methodName));
            throw new IllegalStateException(
                    "Method conflict in event handler with multiple methods with same name. See server log for more details.");
        } else if (methods.size() == 1) {
            return Optional.of(methods.get(0));
        } else if (!Component.class.equals(clazz)) {
            return findMethod(instance, clazz.getSuperclass(), methodName);
        } else {
            return Optional.empty();
        }
    }
}
