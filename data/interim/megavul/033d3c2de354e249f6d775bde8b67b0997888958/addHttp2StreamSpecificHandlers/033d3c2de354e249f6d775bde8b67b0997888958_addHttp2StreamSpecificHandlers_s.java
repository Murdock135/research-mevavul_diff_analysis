class addHttp2StreamSpecificHandlers {
protected void addHttp2StreamSpecificHandlers(ChannelPipeline pipeline)
    {
        pipeline.addLast("h2_metrics_inbound", http2MetricsChannelHandlers.inbound());
        pipeline.addLast("h2_metrics_outbound", http2MetricsChannelHandlers.outbound());
        pipeline.addLast("h2_max_requests_per_conn", connectionExpiryHandler);
        pipeline.addLast("h2_conn_close", connectionCloseHandler);

        pipeline.addLast(http2ResetFrameHandler);
        pipeline.addLast("h2_downgrader", new Http2StreamFrameToHttpObjectCodec(true));
        pipeline.addLast(http2StreamErrorHandler);
        pipeline.addLast(http2StreamHeaderCleaner);
    }
}
