class tfsMaterial {
public static TfsMaterial tfsMaterial(String url) {
        return new TfsMaterial(new UrlArgument(url), "username", "domain", "password", "project-path");
    }
}
