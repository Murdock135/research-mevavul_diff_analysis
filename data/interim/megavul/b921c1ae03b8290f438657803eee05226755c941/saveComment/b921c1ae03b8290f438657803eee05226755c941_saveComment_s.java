class saveComment {
CreateCommentResponse saveComment() {
        CreateCommentRequest createCommentRequest = ZrLogUtil.convertRequestParam(getRequest().getParameterMap(), CreateCommentRequest.class);
        createCommentRequest.setIp(WebTools.getRealIp(getRequest()));
        createCommentRequest.setUserAgent(getHeader("User-Agent"));
        return commentService.save(createCommentRequest);
    }
}
