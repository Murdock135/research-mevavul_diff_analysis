class fileUpload {
@PostMapping("/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws JsonProcessingException {
        if (ConfigConstants.getFileUploadDisable()) {
            return new ObjectMapper().writeValueAsString(ReturnResponse.failure("文件传接口已禁用"));
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        //判断是否为IE浏览器的文件名，IE浏览器下文件名会带有盘符信息

        // escaping dangerous characters to prevent XSS
        assert fileName != null;
        fileName = HtmlUtils.htmlEscape(fileName, StandardCharsets.UTF_8.name());

        // Check for Unix-style path
        int unixSep = fileName.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileName.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (Math.max(winSep, unixSep));
        if (pos != -1) {
            fileName = fileName.substring(pos + 1);
        }
        // 判断是否存在同名文件
        if (existsFile(fileName)) {
            return new ObjectMapper().writeValueAsString(ReturnResponse.failure("存在同名文件，请先删除原有文件再次上传"));
        }
        File outFile = new File(fileDir + demoPath);
        if (!outFile.exists() && !outFile.mkdirs()) {
            logger.error("创建文件夹【{}】失败，请检查目录权限！", fileDir + demoPath);
        }
        logger.info("上传文件：{}", fileDir + demoPath + fileName);
        try (InputStream in = file.getInputStream(); OutputStream out = new FileOutputStream(fileDir + demoPath + fileName)) {
            StreamUtils.copy(in, out);
            return new ObjectMapper().writeValueAsString(ReturnResponse.success(null));
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return new ObjectMapper().writeValueAsString(ReturnResponse.failure());
        }
    }
}
