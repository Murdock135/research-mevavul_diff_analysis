class catchimage {
@RequestMapping(params = "action=" + ACTION_CATCHIMAGE)
    @ResponseBody
    public Map<String, Object> catchimage(@RequestAttribute SysSite site, @SessionAttribute SysUser admin,
            HttpServletRequest request, HttpSession session) {
        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(CommonConstants.defaultRequestConfig)
                .build();) {
            String[] files = request.getParameterValues(FIELD_NAME + "[]");
            if (CommonUtils.notEmpty(files)) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (String image : files) {
                    HttpGet httpget = new HttpGet(image);
                    CloseableHttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();
                    if (null != entity) {
                        BufferedInputStream inputStream = new BufferedInputStream(entity.getContent());
                        FileType fileType = FileTypeDetector.detectFileType(inputStream);
                        String suffix = fileType.getCommonExtension();
                        if (null != fileType.getMimeType() && fileType.getMimeType().startsWith("image/")
                                && CommonUtils.notEmpty(suffix)) {
                            String fileName = CmsFileUtils.getUploadFileName(suffix);
                            String filePath = siteComponent.getWebFilePath(site, fileName);
                            CmsFileUtils.copyInputStreamToFile(inputStream, filePath);
                            FileSize fileSize = CmsFileUtils.getFileSize(filePath, suffix);
                            logUploadService.save(new LogUpload(site.getId(), admin.getId(), LogLoginService.CHANNEL_WEB_MANAGER,
                                    CommonConstants.BLANK, CmsFileUtils.getFileType(suffix), entity.getContentLength(),
                                    fileSize.getWidth(), fileSize.getHeight(), RequestUtils.getIpAddress(request),
                                    CommonUtils.getDate(), fileName));
                            Map<String, Object> map = getResultMap(true);
                            map.put("size", entity.getContentLength());
                            map.put("title", fileName);
                            map.put("url", fileName);
                            map.put("source", image);
                            list.add(map);
                        }

                    }
                    EntityUtils.consume(entity);
                }
                if (list.isEmpty()) {
                    return getResultMap(false);
                } else {
                    Map<String, Object> map = getResultMap(true);
                    map.put("list", list);
                    return map;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getResultMap(false);
        }
        return getResultMap(false);
    }
}
