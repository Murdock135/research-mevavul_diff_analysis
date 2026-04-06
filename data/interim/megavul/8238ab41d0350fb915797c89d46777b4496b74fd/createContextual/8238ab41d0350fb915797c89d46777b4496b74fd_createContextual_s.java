class createContextual {
@Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        // 14-Jun-2017, tatu: [databind#1625]: may want to block merging, for root value
        boolean preventMerge = (property == null)
                && Boolean.FALSE.equals(ctxt.getConfig().getDefaultMergeable(Object.class));
        // 20-Apr-2014, tatu: If nothing custom, let's use "vanilla" instance,
        //     simpler and can avoid some of delegation
        if ((_stringDeserializer == null) && (_numberDeserializer == null)
                && (_mapDeserializer == null) && (_listDeserializer == null)
                &&  getClass() == UntypedObjectDeserializer.class) {
            return Vanilla.instance(preventMerge);
        }

        if (preventMerge != _nonMerging) {
            return new UntypedObjectDeserializer(this, preventMerge);
        }

        return this;
    }
}
