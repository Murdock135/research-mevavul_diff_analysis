class canCameraGestureBeLaunched {
public boolean canCameraGestureBeLaunched(boolean keyguardIsShowing) {
        if (!mStatusBar.isCameraAllowedByAdmin()) {
            EventLog.writeEvent(0x534e4554, "63787722", -1, "");
            return false;
        }

        ResolveInfo resolveInfo = mKeyguardBottomArea.resolveCameraIntent();
        String packageToLaunch = (resolveInfo == null || resolveInfo.activityInfo == null)
                ? null : resolveInfo.activityInfo.packageName;
        return packageToLaunch != null &&
               (keyguardIsShowing || !isForegroundApp(packageToLaunch)) &&
               !mAffordanceHelper.isSwipingInProgress();
    }
}
