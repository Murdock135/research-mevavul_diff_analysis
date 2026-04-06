class generateQuickCode {
public static String generateQuickCode(String nameVal) {
        if (StringUtils.isBlank(nameVal)) return StringUtils.EMPTY;

        if (nameVal.length() > 100) nameVal = nameVal.substring(0, 100);

        if (EasyPhone.isPhone(nameVal) || EasyEmail.isEmail(nameVal) || EasyUrl.isUrl(nameVal)) return StringUtils.EMPTY;

        // 提取 0-9+a-z+A-Z+中文+空格，忽略特殊字符
        nameVal = nameVal.replaceAll("[^a-zA-Z0-9\\s\u4e00-\u9fa5]", "");
        // 忽略数字或小字母
        if (nameVal.matches("[a-z0-9]+")) return StringUtils.EMPTY;

        String quickCode = nameVal;

        if (nameVal.matches("[a-zA-Z0-9\\s]+")) {
            // 仅包含字母数字或空格
        } else {
            // v3.3 拼音全拼
            try {
                quickCode = HanLP.convertToPinyinString(nameVal, "", Boolean.FALSE);
            } catch (Exception e) {
                log.error("QuickCode shorting error : " + nameVal, e);
                quickCode = StringUtils.EMPTY;
            }
        }

        // 去除空格
        quickCode = quickCode.replaceAll(" ", "");
        return CommonsUtils.maxstr(quickCode, 50).toUpperCase();
    }
}
