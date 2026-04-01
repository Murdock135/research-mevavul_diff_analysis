class list_2 {
@RequiresPermissions("tag:list")
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String name, Model model) {
        if (name != null) name = name.replace("\"", "").replace("'", "");
//        name= SecurityUtil.sanitizeInput(name);
        if (StringUtils.isEmpty(name)) name = null;
        IPage<Tag> page = tagService.selectAll(pageNo, null, name);
        model.addAttribute("page", page);
        model.addAttribute("name", name);
        return "admin/tag/list";
    }
}
