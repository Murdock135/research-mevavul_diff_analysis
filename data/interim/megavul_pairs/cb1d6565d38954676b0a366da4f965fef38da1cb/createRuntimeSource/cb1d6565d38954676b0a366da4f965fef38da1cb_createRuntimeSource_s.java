class createRuntimeSource {
private String createRuntimeSource(RuntimeModel model, String baseClassName, boolean scriptInDocs) {
        if (scriptInDocs) {
            throw new RuntimeException("Do no know how to clean the block comments yet");
        }

        SourceWriter writer = new SourceWriter(model);
        writer.setScript(stripComments(theScript));
        writer.setBaseClassName(baseClassName);
        scriptModel.write(writer);
        return writer.getSource();
    }
}
