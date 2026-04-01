class getIcon {
@Override
        public IconState getIcon() {
            ResolveInfo resolved = resolveCameraIntent();
            boolean isCameraDisabled = (mStatusBar != null) && !mStatusBar.isCameraAllowedByAdmin();
            mIconState.isVisible = !isCameraDisabled && resolved != null
                    && getResources().getBoolean(R.bool.config_keyguardShowCameraAffordance)
                    && mUserSetupComplete;
            mIconState.drawable = mContext.getDrawable(R.drawable.ic_camera_alt_24dp);
            mIconState.contentDescription =
                    mContext.getString(R.string.accessibility_camera_button);
            return mIconState;
        }
}
