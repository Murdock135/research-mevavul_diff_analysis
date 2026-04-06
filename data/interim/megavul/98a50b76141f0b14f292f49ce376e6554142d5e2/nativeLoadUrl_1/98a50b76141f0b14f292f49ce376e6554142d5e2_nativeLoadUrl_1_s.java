class nativeLoadUrl_1 {
private native void nativeLoadUrl(
            long nativeContentViewCoreImpl,
            String url,
            int loadUrlType,
            int transitionType,
            String referrerUrl,
            int referrerPolicy,
            int uaOverrideOption,
            String extraHeaders,
            byte[] postData,
            String baseUrlForDataUrl,
            String virtualUrlForDataUrl,
            boolean canLoadLocalResources);
}
