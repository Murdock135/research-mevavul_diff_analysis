class getRelativeAssetsRootPath {
public String getRelativeAssetsRootPath() {
        String path = "";
        path = Config.getStringProperty("ASSET_PATH", DEFAULT_RELATIVE_ASSET_PATH);
        return path;
    }
}
