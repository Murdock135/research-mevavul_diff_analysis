class checkUserPassword {
public boolean checkUserPassword(String userId, String password) {
        if (StringUtils.isBlank(userId)) {
            MSException.throwException(Translator.get("user_name_is_null"));
        }
        if (StringUtils.isBlank(password)) {
            MSException.throwException(Translator.get("password_is_null"));
        }
        if (userId.length() > 64) {
            MSException.throwException(Translator.get("user_id_length_too_long"));
        }
        if (password.length() > 30) {
            MSException.throwException(Translator.get("password_length_too_long"));
        }
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(userId).andPasswordEqualTo(CodingUtil.md5(password));
        return userMapper.countByExample(example) > 0;
    }
}
