class setTextConverter {
@PipelineProperty(description = "The XML fragment wrapper that should be used to wrap the input XML.")
    @PipelinePropertyDocRef(types = TextConverter.ENTITY_TYPE)
    public void setTextConverter(final DocRef textConverterRef) {
        this.textConverterRef = textConverterRef;
    }
}
