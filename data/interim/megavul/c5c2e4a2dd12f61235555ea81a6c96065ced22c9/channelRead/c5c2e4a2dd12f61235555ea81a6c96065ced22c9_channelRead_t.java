class channelRead {
@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            List<String> lengthHeaders = req.headers().getAll(HttpHeaderNames.CONTENT_LENGTH);
            if (lengthHeaders.size() > 1) {
                ctx.writeAndFlush(new DefaultHttp2ResetFrame(Http2Error.PROTOCOL_ERROR));
                return;
            } else if (lengthHeaders.size() == 1) {
                expectedContentLength = Long.parseLong(lengthHeaders.get(0));
                if (expectedContentLength < 0) {
                    // TODO(carl-mastrangelo): this is not right, but meh.  Fix this to return a proper 400.
                    ctx.writeAndFlush(new DefaultHttp2ResetFrame(Http2Error.PROTOCOL_ERROR));
                    return;
                }
            }
            if (hasContentLength() && HttpUtil.isTransferEncodingChunked(req)) {
                // TODO(carl-mastrangelo): this is not right, but meh.  Fix this to return a proper 400.
                ctx.writeAndFlush(new DefaultHttp2ResetFrame(Http2Error.PROTOCOL_ERROR));
                return;
            }
        }
        if (msg instanceof HttpContent) {
            ByteBuf content = ((HttpContent) msg).content();
            incrementSeenContent(content.readableBytes());
            if (hasContentLength() && seenContentLength > expectedContentLength) {
                // TODO(carl-mastrangelo): this is not right, but meh.  Fix this to return a proper 400.
                ctx.writeAndFlush(new DefaultHttp2ResetFrame(Http2Error.PROTOCOL_ERROR));
                return;
            }
        }
        if (msg instanceof LastHttpContent) {
            if (hasContentLength() && seenContentLength != expectedContentLength) {
                // TODO(carl-mastrangelo): this is not right, but meh.  Fix this to return a proper 400.
                ctx.writeAndFlush(new DefaultHttp2ResetFrame(Http2Error.PROTOCOL_ERROR));
                return;
            }
        }
        super.channelRead(ctx, msg);
    }
}
