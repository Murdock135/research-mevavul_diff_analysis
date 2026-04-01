class index {
public String index() {
        if (AdminTokenThreadLocal.getUser() != null) {
            initIndex(getRequest());
            if (getPara(0) == null || getRequest().getRequestURI().endsWith("admin/") || "login".equals(getPara(0))) {
                redirect(Constants.ADMIN_INDEX);
                return null;
            } else {
                if ("dashboard".equals(getPara(0))) {
                    fillStatistics();
                } else if ("website".equals(getPara(0))) {
                    setAttr("templates", templateService.getAllTemplates(getRequest().getContextPath(), TemplateHelper.getTemplatePathByCookie(getRequest().getCookies())));
                }
                return "/admin/" + getPara(0);
            }
        } else {
            return LOGOUT_URI;
        }
    }
}
