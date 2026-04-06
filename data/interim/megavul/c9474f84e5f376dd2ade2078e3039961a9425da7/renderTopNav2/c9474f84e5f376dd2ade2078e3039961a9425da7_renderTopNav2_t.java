class renderTopNav2 {
static String renderTopNav2(HttpServletRequest request) {
        String topNav = KVStorage.getCustomValue("TopNav32");
        if (!JSONUtils.wellFormat(topNav)) return StringUtils.EMPTY;

        JSONArray sets = JSON.parseArray(topNav);
        if (sets.isEmpty()) return StringUtils.EMPTY;

        final ID user = AppUtils.getRequestUser(request);
        final boolean isAdmin = UserHelper.isAdmin(user);
        final Object[][] alls = instance.getAllConfig(null, TYPE_NAV);

        StringBuilder topNavHtml = new StringBuilder();

        for (Object nd : sets) {
            JSONArray ndAnd = (JSONArray) nd;
            String nav = ndAnd.getString(0);
            String dash = ndAnd.getString(1);

            ID useNav = ID.isId(nav) ? ID.valueOf(nav) : null;
            if (useNav == null) continue;

            for (Object[] d : alls) {
                if (!useNav.equals(d[0])) continue;
                // 管理员、有共享的
                if ((isAdmin && RoleService.ADMIN_ROLE.equals(d[5])) || instance.isShareTo((String) d[1], user)) {
                    String url = AppUtils.getContextPath("/app/home?n=" + useNav);
                    if (ID.isId(dash)) url += "&d=" + dash;
                    String name = StringUtils.defaultIfBlank((String) d[4], Language.L("未命名"));

                    topNavHtml.append(
                            String.format("<li class=\"nav-item\" data-id=\"%s\"><a class=\"nav-link text-ellipsis\" href=\"%s\">%s</a></li>", useNav, url, name));
                    break;
                }
            }
        }
        return topNavHtml.toString();
    }
}
