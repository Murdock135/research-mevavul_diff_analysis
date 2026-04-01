class list_1 {
@RequiresPermissions("user:list")
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String username, Model model) {
        if (username != null) username = username.replace("\"", "").replace("'", "");
//        username= SecurityUtil.sanitizeInput(username);
        IPage<User> iPage = userService.selectAll(pageNo, username);
        model.addAttribute("page", iPage);
        model.addAttribute("username", username);
        return "admin/user/list";
    }
}
