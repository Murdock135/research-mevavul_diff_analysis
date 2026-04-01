class getLog {
private Log getLog(AdminTokenVO adminTokenVO, CreateArticleRequest createArticleRequest) {
        Log log = new Log();
        log.set("content", createArticleRequest.getContent());
        log.set("title", createArticleRequest.getTitle());
        log.set("keywords", createArticleRequest.getKeywords());
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
            log.set("thumbnail", getFirstImgUrl(createArticleRequest.getContent(), adminTokenVO));
        } else {
            log.set("thumbnail", createArticleRequest.getThumbnail());
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
        log.set("alias", alias.trim().replace(" ", "-").replace(".", "-"));
        return log;
    }
}
