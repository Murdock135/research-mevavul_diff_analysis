class comment_1 {
@PostMapping("comments")
    @ApiOperation("Comments a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody JournalCommentParam journalCommentParam) {
        return journalCommentService.convertTo(journalCommentService.createBy(journalCommentParam));
    }
}
