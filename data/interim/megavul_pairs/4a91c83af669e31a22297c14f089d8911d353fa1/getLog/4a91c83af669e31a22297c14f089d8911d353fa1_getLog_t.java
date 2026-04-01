class getLog {
private Log getLog(AdminTokenVO adminTokenVO, CreateArticleRequest createArticleRequest) {
        Log log = new Log();
        log.set("content", createArticleRequest.getContent());
        log.set("title", Jsoup.clean(createArticleRequest.getTitle(),Whitelist.basic()));
        log.set("keywords", Jsoup.clean(createArticleRequest.getKeywords(),Whitelist.basic()));
        log.set("markdown", createArticleRequest.getMarkdown());
        log.set("content", createArticleRequest.getContent());
        log.set("userId", adminTokenVO.getUserId());
        log.set("typeId", createArticleRequest.getTypeId());
        log.set("last_update_date", new Date());
        log.set("canComment", createArticleRequest.isCanComment());
        log.set("recommended", createArticleRequest.isRecommended());
        log.set("privacy", createArticleRequest.isPrivacy());
        log.set("rubbish", createArticleRequest.isRubbish());
        if (StringUtils.isEmpty(createArticleRequest.getThumbnail())) {
            log.set("thumbnail", Jsoup.clean(getFirstImgUrl(createArticleRequest.getContent(), adminTokenVO),Whitelist.basic()));
        } else {
            log.set("thumbnail", Jsoup.clean(createArticleRequest.getThumbnail(),Whitelist.basic()));
        }
        // 自动摘要
        if (StringUtils.isEmpty(createArticleRequest.getDigest())) {
            log.set("digest", ParseUtil.autoDigest(log.get("content").toString(), Constants.getAutoDigestLength()));
        } else {
            log.set("digest", createArticleRequest.getDigest());
        }
        log.set("plain_content", getPlainSearchText(log.get("content")));
        log.set("editor_type", createArticleRequest.getEditorType());
        int articleId;
        String alias;
        if (createArticleRequest instanceof UpdateArticleRequest) {
            articleId = ((UpdateArticleRequest) createArticleRequest).getId();
        } else {
            articleId = new Log().findMaxId() + 1;
            log.set("releaseTime", new Date());
        }
        if (createArticleRequest.getAlias() == null) {
            alias = Integer.toString(articleId);
        } else {
            alias = createArticleRequest.getAlias();
        }
        log.set("logId", articleId);
        log.set("alias", Jsoup.clean(alias.trim().replace(" ", "-").replace(".", "-"),Whitelist.basic()));
        return log;
    }
}
