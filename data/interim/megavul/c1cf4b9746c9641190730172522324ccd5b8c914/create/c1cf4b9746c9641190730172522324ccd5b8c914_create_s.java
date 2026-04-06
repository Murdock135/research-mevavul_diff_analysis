class create {
public ResumeMediaBrowser create(ResumeMediaBrowser.Callback callback,
            ComponentName componentName) {
        return new ResumeMediaBrowser(mContext, callback, componentName, mBrowserFactory, mLogger);
    }
}
