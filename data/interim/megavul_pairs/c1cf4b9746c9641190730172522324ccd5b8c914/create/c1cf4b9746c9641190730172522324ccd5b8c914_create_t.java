class create {
public ResumeMediaBrowser create(ResumeMediaBrowser.Callback callback,
            ComponentName componentName, @UserIdInt int userId) {
        return new ResumeMediaBrowser(mContext, callback, componentName, mBrowserFactory, mLogger,
            userId);
    }
}
