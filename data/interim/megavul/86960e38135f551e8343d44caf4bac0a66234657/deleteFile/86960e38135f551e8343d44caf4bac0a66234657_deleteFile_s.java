class deleteFile {
@GetMapping("/deleteFile")
    public String deleteFile(String fileName) throws JsonProcessingException {
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        File file = new File(fileDir + demoPath + fileName);
        logger.info("删除文件：{}", file.getAbsolutePath());
        if (file.exists() && !file.delete()) {
            logger.error("删除文件【{}】失败，请检查目录权限！", file.getPath());
        }
        return new ObjectMapper().writeValueAsString(ReturnResponse.success());
    }
}
