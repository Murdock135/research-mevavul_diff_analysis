class comment_2 {
@PostMapping("comments")
    @ApiOperation("Comments a post")
    @CacheLock(autoDelete = false, traceRequest = true)
    public BaseCommentDTO comment(@RequestBody SheetCommentParam sheetCommentParam) {
        return sheetCommentService.convertTo(sheetCommentService.createBy(sheetCommentParam));
    }
}
