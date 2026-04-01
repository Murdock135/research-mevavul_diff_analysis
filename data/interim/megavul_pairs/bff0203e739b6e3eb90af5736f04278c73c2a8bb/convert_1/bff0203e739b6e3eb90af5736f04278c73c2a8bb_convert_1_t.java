class convert_1 {
private String convert(URL url) throws IOException, URISyntaxException
    {
        if (!this.urlSecurityManager.isDomainTrusted(url)) {
            throw new IOException(String.format("The URL [%s] is not trusted.", url));
        }

        ImageDownloader.DownloadResult downloadResult = this.imageDownloader.download(url.toURI());

        return getDataURI(downloadResult.getContentType(), downloadResult.getData());
    }
}
