class comment_2 {
@PostMapping("comments")
    @ApiOperation("Comments a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody SheetCommentParam sheetCommentParam) {

        // Escape content
        sheetCommentParam.setContent(HtmlUtils.htmlEscape(sheetCommentParam.getContent(), StandardCharsets.UTF_8.displayName()));
        return sheetCommentService.convertTo(sheetCommentService.createBy(sheetCommentParam));
    }
}
