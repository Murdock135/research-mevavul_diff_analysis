class newMergeTargetForField {
@Override
    public MergeTarget newMergeTargetForField(
        Descriptors.FieldDescriptor field, Message defaultInstance) {
      Message.Builder subBuilder;
      if (defaultInstance != null) {
        subBuilder = defaultInstance.newBuilderForType();
      } else {
        subBuilder = builder.newBuilderForField(field);
      }
      if (!field.isRepeated()) {
        Message originalMessage = (Message) getField(field);
        if (originalMessage != null) {
          subBuilder.mergeFrom(originalMessage);
        }
      }
      return new BuilderAdapter(subBuilder);
    }
}
