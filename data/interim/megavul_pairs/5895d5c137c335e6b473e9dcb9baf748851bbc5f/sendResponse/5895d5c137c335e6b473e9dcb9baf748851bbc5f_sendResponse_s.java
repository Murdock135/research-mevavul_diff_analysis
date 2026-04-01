class sendResponse {
private void sendResponse(HttpServletResponse pResp, HttpServletRequest pReq, JSONAware pJson) throws IOException {
        String callback = pReq.getParameter(ConfigKey.CALLBACK.getKeyValue());
        setContentType(pResp, callback != null ? "text/javascript" : getMimeType(pReq));

        pResp.setStatus(HttpServletResponse.SC_OK);
        setNoCacheHeaders(pResp);
        if (pJson == null) {
            pResp.setContentLength(-1);
        } else {
            if (isStreamingEnabled(pReq)) {
                sendStreamingResponse(pResp, callback, (JSONStreamAware) pJson);
            } else {
                // Fallback, send as one object
                // TODO: Remove for 2.0 where should support only streaming
                sendAllJSON(pResp, callback, pJson);
            }
        }
    }
}
