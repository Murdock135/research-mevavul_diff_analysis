class toOldMaterial {
@Override public Material toOldMaterial(String name, String folder, String password) {
        TfsMaterial tfsMaterial = new TfsMaterial(new GoCipher(), new UrlArgument(url), username, domain, password, projectPath);
        tfsMaterial.setFolder(folder);
        setName(name,tfsMaterial);
        return tfsMaterial;
    }
}
