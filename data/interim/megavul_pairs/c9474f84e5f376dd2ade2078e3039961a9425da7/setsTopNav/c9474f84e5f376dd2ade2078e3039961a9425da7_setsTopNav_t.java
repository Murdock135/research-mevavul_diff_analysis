class setsTopNav {
@PostMapping("nav-settings/topnav")
    public RespBody setsTopNav(HttpServletRequest request) {
        if (!UserHelper.isAdmin(getRequestUser(request))) return RespBody.error();

        String s = ServletUtils.getRequestString(request);
        KVStorage.setCustomValue("TopNav32", s);
        return RespBody.ok();
    }
}
