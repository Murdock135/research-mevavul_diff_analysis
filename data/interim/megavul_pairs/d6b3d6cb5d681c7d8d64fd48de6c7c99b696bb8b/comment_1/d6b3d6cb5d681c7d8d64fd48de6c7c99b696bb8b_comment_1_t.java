class comment_1 {
@PostMapping("comments")
    @ApiOperation("Comments a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody JournalCommentParam journalCommentParam) {

        // Escape content
        journalCommentParam.setContent(HtmlUtils.htmlEscape(journalCommentParam.getContent(), StandardCharsets.UTF_8.displayName()));
        return journalCommentService.convertTo(journalCommentService.createBy(journalCommentParam));
    }
}
