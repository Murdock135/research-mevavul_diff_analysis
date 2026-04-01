class fillArticleInfo {
public static void fillArticleInfo(Log data, HttpServletRequest request, String suffix) {
        data.put("alias", data.get("alias") + suffix);
        data.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + data.get("alias"));
        data.put("noSchemeUrl", WebTools.getHomeUrlWithHost(request) + Constants.getArticleUri() + data.get("alias"));
        data.put("typeUrl", WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + data.get("typeAlias") + suffix);
        Log lastLog = data.get("lastLog");
        Log nextLog = data.get("nextLog");
        nextLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + nextLog.get("alias") + suffix);
        lastLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + lastLog.get("alias") + suffix);
        //没有使用md的toc目录的文章才尝试使用系统提取的目录
        if (data.getStr("markdown") != null && !data.getStr("markdown").toLowerCase().contains("[toc]")
                && !data.getStr("markdown").toLowerCase().contains("[tocm]")) {
            //最基础的实现方式，若需要更强大的实现方式建议使用JavaScript完成（页面输入toc对象）
            OutlineVO outlineVO = OutlineUtil.extractOutline(data.getStr("content"));
            if (outlineVO.size() > 0) {
                data.put("tocHtml", OutlineUtil.buildTocHtml(outlineVO, ""));
            }
            data.put("toc", outlineVO);
        }
        if (!new CommentService().isAllowComment()) {
            data.set("canComment", false);
        }
    }
}
