class init {
public void init(FilterConfig config) throws ServletException {
        this.ASSET_PATH = APILocator.getFileAssetAPI().getRealAssetsRootPath();
    }
}
