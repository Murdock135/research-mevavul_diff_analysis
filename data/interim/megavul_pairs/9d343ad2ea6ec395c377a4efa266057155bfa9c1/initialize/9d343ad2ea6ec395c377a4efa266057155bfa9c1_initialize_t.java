class initialize {
public void initialize(ViewGroup containerView, InternalAccessDelegate internalDispatcher,
            WebContents webContents, WindowAndroid windowAndroid) {
        createContentViewAndroidDelegate();
        setContainerView(containerView);
        long windowNativePointer = windowAndroid.getNativePointer();
        assert windowNativePointer != 0;
        createViewAndroid(windowAndroid);

        long viewAndroidNativePointer = mViewAndroid.getNativePointer();
        assert viewAndroidNativePointer != 0;

        mZoomControlsDelegate = NO_OP_ZOOM_CONTROLS_DELEGATE;

        mNativeContentViewCore = nativeInit(
                webContents, viewAndroidNativePointer, windowNativePointer,
                mRetainedJavaScriptObjects);
        mWebContents = nativeGetWebContentsAndroid(mNativeContentViewCore);
        mContentSettings = new ContentSettings(this, mNativeContentViewCore);

        setContainerViewInternals(internalDispatcher);
        mRenderCoordinates.reset();
        initPopupZoomer(mContext);
        mImeAdapter = createImeAdapter(mContext);
        attachImeAdapter();

        mAccessibilityInjector = AccessibilityInjector.newInstance(this);

        mWebContentsObserver = new WebContentsObserver(mWebContents) {
            @Override
            public void didFailLoad(boolean isProvisionalLoad, boolean isMainFrame, int errorCode,
                    String description, String failingUrl) {
                // Navigation that fails the provisional load will have the strong binding removed
                // here. One for which the provisional load is commited will have the strong binding
                // removed in navigationEntryCommitted() below.
                if (isProvisionalLoad) determinedProcessVisibility();
            }

            @Override
            public void didNavigateMainFrame(String url, String baseUrl,
                    boolean isNavigationToDifferentPage, boolean isFragmentNavigation) {
                if (!isNavigationToDifferentPage) return;
                hidePopupsAndClearSelection();
                resetScrollInProgress();
            }

            @Override
            public void renderProcessGone(boolean wasOomProtected) {
                hidePopupsAndClearSelection();
                resetScrollInProgress();
                // No need to reset gesture detection as the detector will have
                // been destroyed in the RenderWidgetHostView.
            }

            @Override
            public void navigationEntryCommitted() {
                determinedProcessVisibility();
            }

            private void determinedProcessVisibility() {
                // Signal to the process management logic that we can now rely on the process
                // visibility signal for binding management. Before the navigation commits, its
                // renderer is considered background even if the pending navigation happens in the
                // foreground renderer.
                ChildProcessLauncher.determinedVisibility(getCurrentRenderProcessId());
            }
        };
    }
}
