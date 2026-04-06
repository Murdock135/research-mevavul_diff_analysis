class update {
@RequiresPermissions("topic:edit")
    @PostMapping("edit")
    @ResponseBody
    public Result update(Integer id, String title, String content, String tags) {
        title = Jsoup.clean(title, Whitelist.basic());
        ApiAssert.notEmpty(title, "话题标题不能为空");

        Topic topic = topicService.selectById(id);
        topic.setTitle(title);
        topic.setContent(content);
        topic.setModifyTime(new Date());
        topicService.update(topic, tags);
        return success();
    }
}
