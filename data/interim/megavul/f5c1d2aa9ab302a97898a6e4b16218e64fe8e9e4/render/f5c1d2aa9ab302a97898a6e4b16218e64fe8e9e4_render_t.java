class render {
@Override
    public void render(HtmlRenderer renderer) {
        if (isArtifactsDeleted || isEmpty()) {
            HtmlElement element = p().unsafecontent("Artifacts for this job instance are unavailable as they may have been <a href='" +
                    CurrentGoCDVersion.docsUrl("configuration/delete_artifacts.html") +
                    "' target='blank'>purged by Go</a> or deleted externally. "
                    + "Re-run the stage or job to generate them again.");
            element.render(renderer);
        }
        for (DirectoryEntry entry : this) {
            entry.toHtml().render(renderer);
        }
    }
}
