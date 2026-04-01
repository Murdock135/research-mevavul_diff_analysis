class startAsCaller {
@Override
        public boolean startAsCaller(Activity activity, Bundle options, int userId) {
            final Intent intent = getBaseIntentToSend();
            if (intent == null) {
                return false;
            }
            intent.setComponent(mChooserTarget.getComponentName());
            intent.putExtras(mChooserTarget.getIntentExtras());

            // Important: we will ignore the target security checks in ActivityManager
            // if and only if the ChooserTarget's target package is the same package
            // where we got the ChooserTargetService that provided it. This lets a
            // ChooserTargetService provide a non-exported or permission-guarded target
            // to the chooser for the user to pick.
            //
            // If mSourceInfo is null, we got this ChooserTarget from the caller or elsewhere
            // so we'll obey the caller's normal security checks.
            final boolean ignoreTargetSecurity = mSourceInfo != null
                    && mSourceInfo.getResolvedComponentName().getPackageName()
                    .equals(mChooserTarget.getComponentName().getPackageName());
            activity.startActivityAsCaller(intent, options, ignoreTargetSecurity, userId);
            return true;
        }
}
