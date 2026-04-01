class generateTooltipHtml {
private String generateTooltipHtml(CmsListInfoBean infoBean) {

        Element root = DOM.createElement("div");
        appendDom(appendDom(root, "p"), "b").setInnerText(CmsClientStringUtil.shortenString(infoBean.getTitle(), 70));
        if (infoBean.hasAdditionalInfo()) {
            for (CmsAdditionalInfoBean additionalInfo : infoBean.getAdditionalInfo()) {
                appendDom(root, "p").setInnerText(
                    additionalInfo.getName()
                        + ":\u00a0"
                        + CmsClientStringUtil.shortenString(additionalInfo.getValue(), 45));
            }
        }
        return root.getInnerHTML();
    }
}
