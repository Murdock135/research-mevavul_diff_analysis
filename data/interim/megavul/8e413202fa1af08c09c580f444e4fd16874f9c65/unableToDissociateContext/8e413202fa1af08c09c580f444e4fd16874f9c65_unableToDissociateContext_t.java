class unableToDissociateContext {
@LogMessage(level = Level.WARN)
    @Message(id = 712, value = "Unable to dissociate context {0} from the storage {1}", format = Format.MESSAGE_FORMAT)
    void unableToDissociateContext(Object context, Object storage);
}
