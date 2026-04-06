class newMergeTargetForField {
@Override
    public MergeTarget newMergeTargetForField(
        Descriptors.FieldDescriptor field, Message defaultInstance) {
      Message.Builder subBuilder;
      if (!field.isRepeated() && hasField(field)) {
        subBuilder = getFieldBuilder(field);
        if (subBuilder != null) {
          return new BuilderAdapter(subBuilder);
        }
      }

      subBuilder = newMessageFieldInstance(field, defaultInstance);
      if (!field.isRepeated()) {
        Message originalMessage = (Message) getField(field);
        if (originalMessage != null) {
          subBuilder.mergeFrom(originalMessage);
        }
      }
      return new BuilderAdapter(subBuilder);
    }
}
