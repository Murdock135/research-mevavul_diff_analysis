class visitorPermission {
private void visitorPermission(Invocation ai) {
        ai.invoke();
        String templateName = ai.getReturnValue();
        if (templateName == null) {
            return;
        }
        GlobalResourceHandler.printUserTime("Template before");
        String templatePath = TemplateHelper.fullTemplateInfo(ai.getController(), true);
        GlobalResourceHandler.printUserTime("Template after");
        TemplateVO templateVO = new TemplateService().getTemplateVO(JFinal.me().getContextPath(), new File(PathKit.getWebRootPath() + templatePath));
        String ext = ZrLogUtil.getViewExt(templateVO.getViewType());
        if (ai.getController().getAttr("log") != null) {
            ai.getController().setAttr("pageLevel", 1);
        } else if (ai.getController().getAttr("data") != null) {
            if ("/".equals(ai.getActionKey()) && new File(PathKit.getWebRootPath() + templatePath + "/" + templateName + ext).exists()) {
                ai.getController().setAttr("pageLevel", 2);
            } else {
                templateName = "page";
                ai.getController().setAttr("pageLevel", 1);
            }
        } else {
            ai.getController().setAttr("pageLevel", 2);
        }
        fullDevData(ai.getController());
        ai.getController().render(templatePath + "/" + templateName + ext);
    }
}
