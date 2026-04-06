class comment {
@PostMapping("comments")
    @ApiOperation("Comments a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody PostCommentParam postCommentParam) {
        postCommentService.validateCommentBlackListStatus();

        // Escape content
        postCommentParam.setContent(HtmlUtils.htmlEscape(postCommentParam.getContent(), StandardCharsets.UTF_8.displayName()));
        return postCommentService.convertTo(postCommentService.createBy(postCommentParam));
    }
}
