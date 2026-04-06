class nativeLoadUrl {
private native int nativeLoadUrl(long nativeTabAndroid, String url, String extraHeaders,
            byte[] postData, int transition, String referrerUrl, int referrerPolicy);
}
