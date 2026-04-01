class generateTooltipHtml {
private String generateTooltipHtml(CmsListInfoBean infoBean) {

        StringBuffer result = new StringBuffer();
        result.append("<p><b>").append(CmsClientStringUtil.shortenString(infoBean.getTitle(), 70)).append("</b></p>");
        if (infoBean.hasAdditionalInfo()) {
            for (CmsAdditionalInfoBean additionalInfo : infoBean.getAdditionalInfo()) {
                result.append("<p>").append(additionalInfo.getName()).append(":&nbsp;");
                // shorten the value to max 45 characters
                result.append(CmsClientStringUtil.shortenString(additionalInfo.getValue(), 45)).append("</p>");
            }
        }
        return result.toString();
    }
}
