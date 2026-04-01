class loadUrl {
public int loadUrl(LoadUrlParams params) {
        TraceEvent.begin();

        // We load the URL from the tab rather than directly from the ContentView so the tab has a
        // chance of using a prerenderer page is any.
        int loadType = nativeLoadUrl(
                mNativeTabAndroid,
                params.getUrl(),
                params.getVerbatimHeaders(),
                params.getPostData(),
                params.getTransitionType(),
                params.getReferrer() != null ? params.getReferrer().getUrl() : null,
                // Policy will be ignored for null referrer url, 0 is just a placeholder.
                // TODO(ppi): Should we pass Referrer jobject and add JNI methods to read it from
                //            the native?
                params.getReferrer() != null ? params.getReferrer().getPolicy() : 0);

        TraceEvent.end();

        for (TabObserver observer : mObservers) {
            observer.onLoadUrl(this, params.getUrl(), loadType);
        }
        return loadType;
    }
}
