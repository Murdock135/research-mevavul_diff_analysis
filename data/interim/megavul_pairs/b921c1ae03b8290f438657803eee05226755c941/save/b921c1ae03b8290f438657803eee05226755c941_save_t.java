class save {
public CreateCommentResponse save(CreateCommentRequest createCommentRequest) {
        CreateCommentResponse createCommentResponse = new CreateCommentResponse();
        if (createCommentRequest.getLogId() != null && createCommentRequest.getComment() != null) {
            if (isAllowComment(Integer.parseInt(createCommentRequest.getLogId()))) {
                String comment = Jsoup.clean(createCommentRequest.getComment(), Whitelist.basic());
                String email = createCommentRequest.getMail();
                if (StringUtils.isNotEmpty(email) || !isValidEmailAddress(email)) {
                    throw new IllegalArgumentException(email + "not email address");
                }
                String nickname = Jsoup.clean(createCommentRequest.getUserName(), Whitelist.basic());
                String userHome = Jsoup.clean(createCommentRequest.getUserHome(), Whitelist.basic());
                if (comment.length() > 0 && !ParseUtil.isGarbageComment(comment)) {
                    new Comment().set("userHome", userHome)
                            .set("userMail", email)
                            .set("userIp", createCommentRequest.getIp())
                            .set("userName", nickname)
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
