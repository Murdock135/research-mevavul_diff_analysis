class getContent {
protected String getContent(SxSource sxSource, FilesystemExportContext exportContext)
    {
        String content;

        // We know we're inside a SX file located at "<S|J>sx/<Space>/<Page>/<s|j>sx<NNN>.<css|js>". Inside this CSS
        // there can be URLs and we need to ensure that the prefix for these URLs lead to the root of the path, i.e.
        // 3 levels up ("../../../").
        // To make this happen we reuse the Doc Parent Level from FileSystemExportContext to a fixed value of 3.
        // We also make sure to put back the original value
        int originalDocParentLevel = exportContext.getDocParentLevel();
        try {
            exportContext.setDocParentLevels(3);
            content = sxSource.getContent();
        } finally {
            exportContext.setDocParentLevels(originalDocParentLevel);
        }

        return content;
    }
}
