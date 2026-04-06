class instantiateRmqObjectMessageWithTrustedPackages {
private static RMQObjectMessage instantiateRmqObjectMessageWithTrustedPackages(List<String> trustedPackages) throws RMQJMSException {
        try {
            // instantiate the message object with the thread context classloader
            Class<?> messageClass = Class.forName(RMQObjectMessage.class.getName(), true, Thread.currentThread().getContextClassLoader());
            Constructor<?> constructor = messageClass.getConstructor(List.class);
            return (RMQObjectMessage) constructor.newInstance(trustedPackages);
        } catch (NoSuchMethodException e) {
            throw new RMQJMSException(e);
        } catch (InvocationTargetException e) {
            throw new RMQJMSException(e);
        } catch (IllegalAccessException e) {
            throw new RMQJMSException(e);
        } catch (InstantiationException e) {
            throw new RMQJMSException(e);
        } catch (ClassNotFoundException e) {
            throw new RMQJMSException(e);
        }
    }
}
