class search {
@GetMapping("/search")
    public String search(@RequestParam(defaultValue = "1") Integer pageNo, @RequestParam String keyword, Model model) {
        model.addAttribute("pageNo", pageNo);
//        model.addAttribute("keyword", SecurityUtil.sanitizeInput(keyword));
        model.addAttribute("keyword", keyword);
        return render("search");
    }
}
