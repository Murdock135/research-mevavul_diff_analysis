class createFrameHandlerFactory {
protected synchronized FrameHandlerFactory createFrameHandlerFactory() throws IOException {
        if(nio) {
            if(this.frameHandlerFactory == null) {
                if(this.nioParams.getNioExecutor() == null && this.nioParams.getThreadFactory() == null) {
                    this.nioParams.setThreadFactory(getThreadFactory());
                }
                this.frameHandlerFactory = new SocketChannelFrameHandlerFactory(connectionTimeout, nioParams, isSSL(), sslContextFactory);
            }
            return this.frameHandlerFactory;
        } else {
            return new SocketFrameHandlerFactory(connectionTimeout, socketFactory, socketConf, isSSL(), this.shutdownExecutor, sslContextFactory);
        }

    }
}
