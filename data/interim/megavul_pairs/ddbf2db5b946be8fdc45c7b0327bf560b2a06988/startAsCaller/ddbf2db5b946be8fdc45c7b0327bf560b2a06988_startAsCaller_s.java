class startAsCaller {
@Override
        public boolean startAsCaller(Activity activity, Bundle options, int userId) {
            final Intent intent = getBaseIntentToSend();
            if (intent == null) {
                return false;
            }
            intent.setComponent(mChooserTarget.getComponentName());
            intent.putExtras(mChooserTarget.getIntentExtras());
            activity.startActivityAsCaller(intent, options, true, userId);
            return true;
        }
}
