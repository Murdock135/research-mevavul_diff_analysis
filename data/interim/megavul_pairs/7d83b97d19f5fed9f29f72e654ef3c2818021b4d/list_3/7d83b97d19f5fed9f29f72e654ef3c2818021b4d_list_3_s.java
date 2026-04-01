class list_3 {
@RequiresPermissions("comment:list")
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String startDate, String endDate, String
            username, Model model) {
//        startDate= SecurityUtil.sanitizeInput(startDate);
//        endDate= SecurityUtil.sanitizeInput(endDate);
//        username=SecurityUtil.sanitizeInput(username);
        if (StringUtils.isEmpty(startDate)) startDate = null;
        if (StringUtils.isEmpty(endDate)) endDate = null;
        if (StringUtils.isEmpty(username)) username = null;
        MyPage<Map<String, Object>> page = commentService.selectAllForAdmin(pageNo, startDate, endDate, username);
        model.addAttribute("page", page);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("username", username);
        return "admin/comment/list";
    }
}
