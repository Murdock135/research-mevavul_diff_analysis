class instantiateRmqObjectMessageWithTrustedPackages {
private static RMQObjectMessage instantiateRmqObjectMessageWithTrustedPackages(List<String> trustedPackages) throws RMQJMSException {
        return (RMQObjectMessage) instantiateRmqMessageWithTrustedPackages(RMQObjectMessage.class.getName(), trustedPackages);
    }
}
