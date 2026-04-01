class download {
private File download(String driverFileUrl, String parentDir) {
        Path parentDirPath = Paths.get(parentDir);
        try {
            Files.createDirectories(parentDirPath);
        } catch (IOException e) {
            log.error("create directory for driver failed", e);
            throw DomainErrors.DOWNLOAD_DRIVER_ERROR.exception(e);
        }

        // download
        try {
            return restTemplate.execute(driverFileUrl, HttpMethod.GET, null, response -> {
                if (response.getStatusCode().is2xxSuccessful()) {
                    String prefix = System.currentTimeMillis() + "";
                    String originFileName = response.getHeaders().getContentDisposition().getFilename();
                    String filename;
                    if (originFileName == null) {
                        URL url = new URL(driverFileUrl);
                        String nameFromUrl = FilenameUtils.getName(url.getPath());
                        if (StringUtils.endsWith(nameFromUrl, ".jar")) {
                            filename = prefix + "-" + nameFromUrl;
                        } else {
                            filename = prefix + ".jar";
                        }
                    } else {
                        filename = prefix + "-" + originFileName;
                    }
                    File targetFile = Paths.get(parentDir, filename).toFile();
                    FileOutputStream out = new FileOutputStream(targetFile);
                    StreamUtils.copy(response.getBody(), out);
                    IOUtils.closeQuietly(out, ex -> log.error("close file error", ex));
                    log.info("{} download success ", targetFile);
                    return targetFile;
                } else {
                    log.error("{} download error from {}: {} ", parentDir, driverFileUrl, response);
                    throw DomainErrors.DOWNLOAD_DRIVER_ERROR.exception("驱动下载失败："
                            + response.getStatusCode()
                            + ", "
                            + response.getStatusText());
                }
            });
        } catch (RestClientException e) {
            String msg = String.format("download driver from %s to %s failed", driverFileUrl, parentDir);
            log.error(msg, e);
            throw DomainErrors.DOWNLOAD_DRIVER_ERROR.exception(msg);
        }
    }
}
