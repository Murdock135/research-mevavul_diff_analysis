class getAppLabel {
private CharSequence getAppLabel(String appPackage) {
        PackageManager pm = mContext.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(appPackage, 0);
            String label = appInfo.loadLabel(pm).toString();
            return convertSafeLabel(label, appPackage);
        } catch (PackageManager.NameNotFoundException e) {
            Rlog.e(TAG, "PackageManager Name Not Found for package " + appPackage);
            return appPackage;  // fall back to package name if we can't get app label
        }
    }
}
