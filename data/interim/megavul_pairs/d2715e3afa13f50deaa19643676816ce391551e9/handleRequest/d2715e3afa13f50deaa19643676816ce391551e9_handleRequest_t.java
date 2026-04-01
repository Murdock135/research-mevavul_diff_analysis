class handleRequest {
@Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        final String path = exchange.getRelativePath();
        if(isForbiddenPath(path)) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            return;
        }
        final ServletPathMatch info = paths.getServletHandlerByPath(path);
        if (info.getType() == ServletPathMatch.Type.REWRITE) {
            // this can only happen if the path ends with a /
            // otherwise there would be a redirect instead
            exchange.setRelativePath(info.getRewriteLocation());
            exchange.setRequestPath(exchange.getResolvedPath() + info.getRewriteLocation());
        }
        final HttpServletResponseImpl response = new HttpServletResponseImpl(exchange, servletContext);
        final HttpServletRequestImpl request = new HttpServletRequestImpl(exchange, servletContext);
        final ServletRequestContext servletRequestContext = new ServletRequestContext(servletContext.getDeployment(), request, response, info);
        //set the max request size if applicable
        if (info.getServletChain().getManagedServlet().getMaxRequestSize() > 0) {
            exchange.setMaxEntitySize(info.getServletChain().getManagedServlet().getMaxRequestSize());
        }
        exchange.putAttachment(ServletRequestContext.ATTACHMENT_KEY, servletRequestContext);

        exchange.startBlocking(new ServletBlockingHttpExchange(exchange));
        servletRequestContext.setServletPathMatch(info);

        Executor executor = info.getServletChain().getExecutor();
        if (executor == null) {
            executor = servletContext.getDeployment().getExecutor();
        }

        if (exchange.isInIoThread() || executor != null) {
            //either the exchange has not been dispatched yet, or we need to use a special executor
            exchange.dispatch(executor, dispatchHandler);
        } else {
            dispatchRequest(exchange, servletRequestContext, info.getServletChain(), DispatcherType.REQUEST);
        }
    }
}
