class mergeMessage {
@Override
    public void mergeMessage(
        CodedInputStream input,
        ExtensionRegistryLite extensionRegistry,
        Descriptors.FieldDescriptor field,
        Message defaultInstance)
        throws IOException {
      if (!field.isRepeated()) {
        Message.Builder subBuilder;
        if (hasField(field)) {
          subBuilder = getFieldBuilder(field);
          if (subBuilder != null) {
            input.readMessage(subBuilder, extensionRegistry);
            return;
          } else {
            subBuilder = newMessageFieldInstance(field, defaultInstance);
            subBuilder.mergeFrom((Message) getField(field));
          }
        } else {
          subBuilder = newMessageFieldInstance(field, defaultInstance);
        }
        input.readMessage(subBuilder, extensionRegistry);
        Object unused = setField(field, subBuilder.buildPartial());
      } else {
        Message.Builder subBuilder = newMessageFieldInstance(field, defaultInstance);
        input.readMessage(subBuilder, extensionRegistry);
        Object unused = addRepeatedField(field, subBuilder.buildPartial());
      }
    }
}
