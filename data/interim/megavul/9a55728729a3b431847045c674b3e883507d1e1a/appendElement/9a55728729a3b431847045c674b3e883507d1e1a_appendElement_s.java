class appendElement {
private void appendElement(StringBuilder breadcrumb, BrowseBarElement element) {
    if (breadcrumb.length() > 0) {
      breadcrumb.append(CONNECTOR);
    }
    if (StringUtil.isDefined(element.getLink())) {
      breadcrumb.append("<a href=\"").append(element.getLink()).append("\"");
    } else {
      breadcrumb.append("<span");
    }
    breadcrumb.append(" class=\"element\"");
    if (StringUtil.isDefined(element.getId())) {
      breadcrumb.append(" id=\"").append(element.getId()).append("\"");
    }
    breadcrumb.append(">");
    breadcrumb.append(WebEncodeHelper.javaStringToHtmlString(element.getLabel()));
    if (StringUtil.isDefined(element.getLink())) {
      breadcrumb.append("</a>");
    } else {
      breadcrumb.append("</span>");
    }
  }
}
