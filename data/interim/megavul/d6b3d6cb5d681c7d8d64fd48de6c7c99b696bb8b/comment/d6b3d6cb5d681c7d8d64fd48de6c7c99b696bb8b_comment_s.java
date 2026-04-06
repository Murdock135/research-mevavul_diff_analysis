class comment {
@PostMapping("comments")
    @ApiOperation("Comments a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody PostCommentParam postCommentParam) {
        postCommentService.validateCommentBlackListStatus();
        return postCommentService.convertTo(postCommentService.createBy(postCommentParam));
    }
}
