class list {
@RequiresPermissions("topic:list")
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String startDate, String endDate, String
            username, Model model) {
        if (StringUtils.isEmpty(startDate)) startDate = null;
        if (StringUtils.isEmpty(endDate)) endDate = null;
        if (StringUtils.isEmpty(username)) username = null;
        MyPage<Map<String, Object>> page = topicService.selectAllForAdmin(pageNo, startDate, endDate, username);
        model.addAttribute("page", page);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("username", username);
        return "admin/topic/list";
    }
}
