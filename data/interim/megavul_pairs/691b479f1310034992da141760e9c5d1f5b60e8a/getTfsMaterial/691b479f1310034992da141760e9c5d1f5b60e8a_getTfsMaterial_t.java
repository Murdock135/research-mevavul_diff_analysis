class getTfsMaterial {
public TfsMaterial getTfsMaterial() {
        return getExistingOrDefaultMaterial(new TfsMaterial(new UrlArgument(""), "", "", "", ""));
    }
}
