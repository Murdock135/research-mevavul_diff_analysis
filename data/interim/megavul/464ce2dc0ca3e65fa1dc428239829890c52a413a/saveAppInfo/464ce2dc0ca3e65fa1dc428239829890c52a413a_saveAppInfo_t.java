class saveAppInfo {
@PostMapping("/save")
    public ResultDTO<Void> saveAppInfo(@RequestBody ModifyAppInfoRequest req) {

        req.valid();
        AppInfoDO appInfoDO;

        Long id = req.getId();
        if (id == null) {
            appInfoDO = new AppInfoDO();
            appInfoDO.setGmtCreate(new Date());
        }else {
            appInfoDO = appInfoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("can't find appInfo by id:" + id));

            // 对比密码
            if (!Objects.equals(req.getOldPassword(), appInfoDO.getPassword())) {
                throw new PowerJobException("The password is incorrect.");
            }
        }
        BeanUtils.copyProperties(req, appInfoDO);
        appInfoDO.setGmtModified(new Date());

        appInfoRepository.saveAndFlush(appInfoDO);
        return ResultDTO.success(null);
    }
}
