class createRuntimeSource {
private String createRuntimeSource(RuntimeModel model, String baseClassName,
                                       boolean scriptInDocs) {
        SourceWriter writer = new SourceWriter(model);
        if (scriptInDocs) {
            writer.setScript(theScript);
        }
        writer.setBaseClassName(baseClassName);
        scriptModel.write(writer);
        return writer.getSource();
    }
}
