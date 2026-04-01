class get {
@Override
    public Container get(ContainerType containerType) {
        return jpaContainerProvider.get(OptionEntity_LongInt.class, containerType);
    }
}
