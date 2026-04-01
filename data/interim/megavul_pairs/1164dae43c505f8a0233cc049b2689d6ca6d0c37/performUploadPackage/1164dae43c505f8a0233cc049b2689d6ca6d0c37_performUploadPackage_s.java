class performUploadPackage {
protected UploadPackageResultDto performUploadPackage(MultipartFile pluginPackageFile, File localFilePath) {
        String pluginPackageFileName = pluginPackageFile.getName();
        if (!localFilePath.exists()) {
            if (localFilePath.mkdirs()) {
                log.info("Create directory [{}] successful", localFilePath.getAbsolutePath());
            } else {
                String errMsg = String.format("Create directory [%s] failed.", localFilePath.getAbsolutePath());
                throw new WecubeCoreException("3099", errMsg, localFilePath.getAbsolutePath());
            }
        }

        File dest = new File(localFilePath, "/" + pluginPackageFileName);
        try {
            log.info("new file location: {}, filename: {}, canonicalpath: {}, canonicalfilename: {}",
                    dest.getAbsoluteFile(), dest.getName(), dest.getCanonicalPath(), dest.getCanonicalFile().getName());
            pluginPackageFile.transferTo(dest);
        } catch (IOException e) {
            log.error("errors to transfer uploaded files.", e);
            throw new WecubeCoreException("Failed to upload package,due to " + e.getMessage());
        }

        UploadPackageResultDto result = null;
        try {
            result = parsePackageFile(dest, localFilePath);
            log.info("Package uploaded successfully.");
        } catch (Exception e) {
            log.error("Errors to parse uploaded package file.", e);
            throw new WecubeCoreException("Failed to upload package due to " + e.getMessage());
        }

        return result;
    }
}
