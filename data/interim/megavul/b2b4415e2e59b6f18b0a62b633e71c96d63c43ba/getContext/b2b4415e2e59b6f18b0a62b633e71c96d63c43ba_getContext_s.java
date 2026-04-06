class getContext {
public static CloseResponseHandle getContext(String uri, String method, HttpServletRequest request, boolean disableRedirect) throws IOException, InstantiationException {
        String pluginServerHttp = Constants.pluginServer;
        CloseableHttpResponse httpResponse;
        CloseResponseHandle handle = new CloseResponseHandle();
        HttpUtil httpUtil = disableRedirect ? HttpUtil.getDisableRedirectInstance() : HttpUtil.getInstance();
        //GET请求不关心request.getInputStream() 的数据
        if (method.equals(request.getMethod()) && "GET".equalsIgnoreCase(method)) {
            httpResponse = httpUtil.sendGetRequest(pluginServerHttp + uri, request.getParameterMap(), handle, PluginHelper.genHeaderMapByRequest(request)).getT();
        } else {
            //如果是表单数据提交不关心请求头，反之将所有请求头都发到插件服务
            if ("application/x-www-form-urlencoded".equals(request.getContentType())) {
                httpResponse = httpUtil.sendPostRequest(pluginServerHttp + uri, request.getParameterMap(), handle, PluginHelper.genHeaderMapByRequest(request)).getT();
            } else {
                httpResponse = httpUtil.sendPostRequest(pluginServerHttp + uri + "?" + request.getQueryString(), IOUtil.getByteByInputStream(request.getInputStream()), handle, PluginHelper.genHeaderMapByRequest(request)).getT();
            }
        }
        //添加插件服务的HTTP响应头到调用者响应头里面
        if (httpResponse != null) {
            Map<String, String> headerMap = new HashMap<>();
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }
            if (JFinal.me().getConstants().getDevMode()) {
                LOGGER.info(uri + " --------------------------------- response");
            }
            for (Map.Entry<String, String> t : headerMap.entrySet()) {
                if (JFinal.me().getConstants().getDevMode()) {
                    LOGGER.info("key " + t.getKey() + " value-> " + t.getValue());
                }
            }
        }
        return handle;
    }
}
