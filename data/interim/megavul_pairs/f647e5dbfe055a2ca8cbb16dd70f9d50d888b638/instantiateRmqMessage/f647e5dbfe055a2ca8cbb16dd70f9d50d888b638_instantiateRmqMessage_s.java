class instantiateRmqMessage {
private static RMQMessage instantiateRmqMessage(String messageClass, List<String> trustedPackages) throws RMQJMSException {
        if(isRmqObjectMessageClass(messageClass)) {
            return instantiateRmqObjectMessageWithTrustedPackages(trustedPackages);
        } else {
            try {
                // instantiate the message object with the thread context classloader
                return (RMQMessage) Class.forName(messageClass, true, Thread.currentThread().getContextClassLoader()).getDeclaredConstructor().newInstance();
            } catch (InstantiationException e) {
                throw new RMQJMSException(e);
            } catch (IllegalAccessException e) {
                throw new RMQJMSException(e);
            } catch (ClassNotFoundException e) {
                throw new RMQJMSException(e);
            } catch (NoSuchMethodException e) {
                throw new RMQJMSException(e);
            } catch (InvocationTargetException e) {
                throw new RMQJMSException(e);
            }
        }
    }
}
