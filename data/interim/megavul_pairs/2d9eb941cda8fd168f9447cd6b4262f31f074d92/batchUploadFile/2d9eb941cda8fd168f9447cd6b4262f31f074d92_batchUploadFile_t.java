class batchUploadFile {
@Override
    public String batchUploadFile(HttpServletRequest request, List<MultipartFile> filedatas, SystemConfig systemConfig) {

        String uploadQiNiu = systemConfig.getUploadQiNiu();
        String uploadLocal = systemConfig.getUploadLocal();
        String uploadMinio = systemConfig.getUploadMinio();

        // 判断来源
        String source = request.getParameter(SysConf.SOURCE);
        //如果是用户上传，则包含用户uid
        String userUid = "";
        //如果是管理员上传，则包含管理员uid
        String adminUid = "";
        //项目名
        String projectName = "";
        //模块名
        String sortName = "";

        // 判断图片来源
        if (SysConf.PICTURE.equals(source)) {
            // 当从vue-mogu-web网站过来的，直接从参数中获取
            userUid = request.getParameter(SysConf.USER_UID);
            adminUid = request.getParameter(SysConf.ADMIN_UID);
            projectName = request.getParameter(SysConf.PROJECT_NAME);
            sortName = request.getParameter(SysConf.SORT_NAME);
        } else if (SysConf.ADMIN.equals(source)) {
            // 当图片从mogu-admin传递过来的时候
            userUid = request.getAttribute(SysConf.USER_UID).toString();
            adminUid = request.getAttribute(SysConf.ADMIN_UID).toString();
            projectName = request.getAttribute(SysConf.PROJECT_NAME).toString();
            sortName = request.getAttribute(SysConf.SORT_NAME).toString();
        } else {
            userUid = request.getAttribute(SysConf.USER_UID).toString();
            adminUid = request.getAttribute(SysConf.ADMIN_UID).toString();
            projectName = request.getAttribute(SysConf.PROJECT_NAME).toString();
            sortName = request.getAttribute(SysConf.SORT_NAME).toString();
        }

        //projectName现在默认base
        if (StringUtils.isEmpty(projectName)) {
            projectName = "base";
        }

        //TODO 检测用户上传，如果不是网站的用户就不能调用
        if (StringUtils.isEmpty(userUid) && StringUtils.isEmpty(adminUid)) {
            return ResultUtil.result(SysConf.ERROR, "请先注册");
        }

        QueryWrapper<FileSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.SORT_NAME, sortName);
        queryWrapper.eq(SQLConf.PROJECT_NAME, projectName);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<FileSort> fileSorts = fileSortService.list(queryWrapper);

        FileSort fileSort = null;
        if (fileSorts.size() >= 1) {
            fileSort = fileSorts.get(0);
        } else {
            return ResultUtil.result(SysConf.ERROR, "文件不被允许上传");
        }

        String sortUrl = fileSort.getUrl();
        //判断url是否为空，如果为空，使用默认
        if (StringUtils.isEmpty(sortUrl)) {
            sortUrl = "base/common/";
        } else {
            sortUrl = fileSort.getUrl();
        }

        List<File> lists = new ArrayList<>();
        //文件上传
        if (filedatas != null && filedatas.size() > 0) {
            for (MultipartFile filedata : filedatas) {
                String oldName = filedata.getOriginalFilename();
                long size = filedata.getSize();
                //获取扩展名，默认是jpg
                String picExpandedName = FileUtils.getPicExpandedName(oldName);
                // 检查是否是安全的格式
                if (!FileUtils.isPic(picExpandedName)) {
                    throw new InsertException("请上传正确格式的文件");
                }

                //获取新文件名
                String newFileName = System.currentTimeMillis() + Constants.SYMBOL_POINT + picExpandedName;
                String localUrl = "";
                String qiNiuUrl = "";
                String minioUrl = "";
                try {
                    MultipartFile tempFileData = filedata;
                    // 上传七牛云，判断是否能够上传七牛云
                    if (EOpenStatus.OPEN.equals(uploadQiNiu)) {
                        qiNiuUrl = qiniuService.uploadFile(tempFileData);
                    }

                    // 判断是否能够上传Minio文件服务器
                    if (EOpenStatus.OPEN.equals(uploadMinio)) {
                        minioUrl = minioService.uploadFile(tempFileData);
                    }

                    // 判断是否能够上传至本地
                    if (EOpenStatus.OPEN.equals(uploadLocal)) {
                        localUrl = localFileService.uploadFile(filedata, fileSort);
                    }
                } catch (Exception e) {
                    log.info("上传文件异常: {}", e.getMessage());
                    e.getStackTrace();
                    return ResultUtil.result(SysConf.ERROR, "文件上传失败，请检查系统配置");
                }

                File file = new File();
                file.setCreateTime(new Date(System.currentTimeMillis()));
                file.setFileSortUid(fileSort.getUid());
                file.setFileOldName(oldName);
                file.setFileSize(size);
                file.setPicExpandedName(picExpandedName);
                file.setPicName(newFileName);
                file.setPicUrl(localUrl);
                file.setStatus(EStatus.ENABLE);
                file.setUserUid(userUid);
                file.setAdminUid(adminUid);
                file.setQiNiuUrl(qiNiuUrl);
                file.setMinioUrl(minioUrl);
                file.insert();
                lists.add(file);
            }
            //保存成功返回数据
            return ResultUtil.result(SysConf.SUCCESS, lists);
        }
        return ResultUtil.result(SysConf.ERROR, "请上传图片");
    }
}
