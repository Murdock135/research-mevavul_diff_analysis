class getRelativeAssetsRootPath {
public String getRelativeAssetsRootPath() {
        String path = "";
        path = Config.getStringProperty("ASSET_PATH");
        return path;
    }
}
