class resetPwd {
@Override
    public boolean resetPwd(ResetPwdRequest request, UserDto currentUser) {
        Optional.ofNullable(currentUser).orElseThrow(() -> new RuntimeException("当前用户为空"));
        User user = this.getUserById(currentUser.getId());
        Optional.ofNullable(user).orElseThrow(() -> new RuntimeException("当前登录用户不存在"));

        // 非本地创建用户不允许修改密码
        if (!"local".equalsIgnoreCase(user.getSource())) {
            throw new RuntimeException("非云管本地创建的用户无法修改密码");
        }
        if (StringUtils.equals(request.getOldPassword(), request.getNewPassword())) {
            throw new RuntimeException("新旧密码相同");
        }
        if (!MD5Util.md5(request.getOldPassword()).equalsIgnoreCase(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        if (!request.getNewPassword().matches("^(?!.*\\s)(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[\\W_]).{8,30}$")) {
            throw new RuntimeException("有效密码：8-30位，英文大小写字母+数字+特殊字符");
        }

        user.setPassword(MD5Util.md5(request.getNewPassword()));
        user.setUpdateTime(null);
        this.updateById(user);
        return true;
    }
}
