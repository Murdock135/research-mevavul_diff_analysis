class generateQuickCode {
public static String generateQuickCode(String nameVal) {
        if (StringUtils.isBlank(nameVal)) return StringUtils.EMPTY;

        if (nameVal.length() > 100) nameVal = nameVal.substring(0, 100);

        if (EasyPhone.isPhone(nameVal) || EasyEmail.isEmail(nameVal) || EasyUrl.isUrl(nameVal)) return StringUtils.EMPTY;

        // 提取 0-9+a-z+A-Z+中文+空格，忽略特殊字符
        nameVal = nameVal.replaceAll("[^a-zA-Z0-9\\s\u4e00-\u9fa5]", "");
        // 忽略数字或小字母
        if (nameVal.matches("[a-z0-9]+")) return StringUtils.EMPTY;

        String quickCode = StringUtils.EMPTY;

        // 仅包含字母数字或空格
        if (nameVal.matches("[a-zA-Z0-9\\s]+")) {
            // 提取英文单词的首字母
            String[] asplit = nameVal.split("(?=[A-Z\\s])");
            if (asplit.length == 1) {
                quickCode = nameVal;
            } else {
                StringBuilder sb = new StringBuilder();
                for (String a : asplit) {
                    if (a.trim().length() > 0) {
                        sb.append(a.trim(), 0, 1);
                    }
                }
                quickCode = sb.toString();
            }

        } else {
            // 拼音首字母
            nameVal = nameVal.replaceAll(" ", "");
            try {
                quickCode = HanLP.convertToPinyinFirstCharString(nameVal, "", false);
            } catch (Exception e) {
                log.error("QuickCode shorting error : " + nameVal, e);
            }
        }

        return CommonsUtils.maxstr(quickCode, 50).toUpperCase();
    }
}
