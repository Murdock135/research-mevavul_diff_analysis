class loadUrl_1 {
public void loadUrl(LoadUrlParams params) {
        if (mNativeContentViewCore == 0) return;

        nativeLoadUrl(mNativeContentViewCore,
                params.mUrl,
                params.mLoadUrlType,
                params.mTransitionType,
                params.getReferrer() != null ? params.getReferrer().getUrl() : null,
                params.getReferrer() != null ? params.getReferrer().getPolicy() : 0,
                params.mUaOverrideOption,
                params.getExtraHeadersString(),
                params.mPostData,
                params.mBaseUrlForDataUrl,
                params.mVirtualUrlForDataUrl,
                params.mCanLoadLocalResources);
    }
}
