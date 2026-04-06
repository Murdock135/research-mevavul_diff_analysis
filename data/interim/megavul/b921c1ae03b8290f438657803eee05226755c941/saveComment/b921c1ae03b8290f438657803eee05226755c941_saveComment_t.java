class saveComment {
CreateCommentResponse saveComment() {
        CreateCommentRequest createCommentRequest = ZrLogUtil.convertRequestParam(getRequest().getParameterMap(), CreateCommentRequest.class);
        createCommentRequest.setIp(WebTools.getRealIp(getRequest()));
        createCommentRequest.setUserAgent(Jsoup.clean(getHeader("User-Agent"), Whitelist.basic()));
        return commentService.save(createCommentRequest);
    }
}
