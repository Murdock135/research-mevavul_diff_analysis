class mergeMessage {
@Override
    public void mergeMessage(
        CodedInputStream input,
        ExtensionRegistryLite extensionRegistry,
        Descriptors.FieldDescriptor field,
        Message defaultInstance)
        throws IOException {
      if (!field.isRepeated()) {
        if (hasField(field)) {
          input.readMessage(builder.getFieldBuilder(field), extensionRegistry);
          return;
        }
        Message.Builder subBuilder = newMessageFieldInstance(field, defaultInstance);
        input.readMessage(subBuilder, extensionRegistry);
        Object unused = setField(field, subBuilder.buildPartial());
      } else {
        Message.Builder subBuilder = newMessageFieldInstance(field, defaultInstance);
        input.readMessage(subBuilder, extensionRegistry);
        Object unused = addRepeatedField(field, subBuilder.buildPartial());
      }
    }
}
