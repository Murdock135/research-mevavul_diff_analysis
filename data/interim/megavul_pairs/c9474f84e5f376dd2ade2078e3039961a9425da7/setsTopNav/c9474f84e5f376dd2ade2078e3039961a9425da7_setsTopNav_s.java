class setsTopNav {
@PostMapping("nav-settings/topnav")
    public RespBody setsTopNav(HttpServletRequest request) {
        String s = ServletUtils.getRequestString(request);
        KVStorage.setCustomValue("TopNav32", s);
        return RespBody.ok();
    }
}
