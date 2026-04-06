class save {
public CreateCommentResponse save(CreateCommentRequest createCommentRequest) {
        CreateCommentResponse createCommentResponse = new CreateCommentResponse();
        if (createCommentRequest.getLogId() != null && createCommentRequest.getComment() != null) {
            if (isAllowComment(Integer.valueOf(createCommentRequest.getLogId()))) {
                String comment = Jsoup.clean(createCommentRequest.getComment(), Whitelist.basic());
                if (comment.length() > 0 && !ParseUtil.isGarbageComment(comment)) {
                    new Comment().set("userHome", createCommentRequest.getUserHome())
                            .set("userMail", createCommentRequest.getComment())
                            .set("userIp", createCommentRequest.getIp())
                            .set("userName", createCommentRequest.getUserName())
                            .set("logId", createCommentRequest.getLogId())
                            .set("userComment", comment)
                            .set("user_agent", createCommentRequest.getUserAgent())
                            .set("reply_id", createCommentRequest.getReplyId())
                            .set("commTime", new Date()).set("hide", 1).save();
                } else {
                    createCommentResponse.setError(1);
                    createCommentResponse.setMessage("");
                }
            } else {
                createCommentResponse.setError(1);
                createCommentResponse.setMessage("");
            }
        } else {
            createCommentResponse.setError(1);
            createCommentResponse.setMessage("");
        }
        Log log = new Log().findByIdOrAlias(createCommentRequest.getLogId());
        if (log != null) {
            createCommentResponse.setAlias(log.getStr("alias"));
        }
        return createCommentResponse;
    }
}
