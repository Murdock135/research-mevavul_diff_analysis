class loadFileAsBytes {
public byte[] loadFileAsBytes(FileOperationRequest fileOperationRequest) {
        File file = new File(FileUtils.BODY_FILE_DIR + "/" + fileOperationRequest.getId() + "_" + fileOperationRequest.getName());
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);) {
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            return bos.toByteArray();
        } catch (Exception ex) {
            LogUtil.error(ex);
        }
        return null;
    }
}
