class setContent {
public void setContent(String content) {
        //xss过滤
        if (StringUtils.isNotBlank(content)) {
            this.content = XSSUtil.stripXSS(content);
        } else {
            this.content = content;
        }
    }
}
