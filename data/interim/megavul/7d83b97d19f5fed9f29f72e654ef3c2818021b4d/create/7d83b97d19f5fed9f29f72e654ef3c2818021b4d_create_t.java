class create {
@PostMapping
    public Result create(@RequestBody Map<String, String> body) {
        User user = getApiUser();
        ApiAssert.isTrue(user.getActive(), "你的帐号还没有激活，请去个人设置页面激活帐号");
        String title = body.get("title");
        String content = body.get("content");
        String tag = body.get("tag");
        //    String tags = body.get("tags");
        title = Jsoup.clean(title, Whitelist.basic());
        ApiAssert.notEmpty(title, "请输入标题");
        ApiAssert.isNull(topicService.selectByTitle(title), "话题标题重复");
        //    String[] strings = StringUtils.commaDelimitedListToStringArray(tags);
        //    Set<String> set = StringUtil.removeEmpty(strings);
        //    ApiAssert.notTrue(set.isEmpty() || set.size() > 5, "请输入标签且标签最多5个");
        // 保存话题
        // 再次将tag转成逗号隔开的字符串
        //    tags = StringUtils.collectionToCommaDelimitedString(set);
        Topic topic = topicService.insert(title, content, tag, user);
        topic.setContent(SensitiveWordUtil.replaceSensitiveWord(topic.getContent(), "*", SensitiveWordUtil.MinMatchType));
        return success(topic);
    }
}
