class canCameraGestureBeLaunched {
public boolean canCameraGestureBeLaunched(boolean keyguardIsShowing) {
        ResolveInfo resolveInfo = mKeyguardBottomArea.resolveCameraIntent();
        String packageToLaunch = (resolveInfo == null || resolveInfo.activityInfo == null)
                ? null : resolveInfo.activityInfo.packageName;
        return packageToLaunch != null &&
               (keyguardIsShowing || !isForegroundApp(packageToLaunch)) &&
               !mAffordanceHelper.isSwipingInProgress();
    }
}
