class addActionsToNotification {
public static void addActionsToNotification(PushActionsProvider provider, String categoryId, NotificationCompat.Builder builder, Intent targetIntent, Context context) throws IOException {
        // NOTE:  THis will likely run when the main activity isn't running so we won't have
        // access to any display properties... just native Android APIs will be accessible.
        
        PushActionCategory category = null;
        PushActionCategory[] categories;
        if (provider != null) {
            categories = provider.getPushActionCategories();
        } else {
            categories = getInstalledPushActionCategories(context);
        }
        for (PushActionCategory candidateCategory : categories) {
            if (categoryId.equals(candidateCategory.getId())) {
                category = candidateCategory;
                break;
            }
        }
        if (category == null) {
            return;
        }
        
        int requestCode = 1;
        for (PushAction action : category.getActions()) {
            Intent newIntent = (Intent)targetIntent.clone();
            newIntent.putExtra("pushActionId", action.getId());
            PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode++, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            try {
                int iconId = 0;
                try { iconId = Integer.parseInt(action.getIcon());} catch (Exception ex){}
                //android.app.Notification.Action.Builder actionBuilder = new android.app.Notification.Action.Builder(iconId, action.getTitle(), contentIntent);

                System.out.println("Adding action "+action.getId()+", "+action.getTitle()+", icon="+iconId);
                if (ActionWrapper.BuilderWrapper.isSupported()) {
                    // We need to take this abstracted "wrapper" approach because the Action.Builder class, and RemoteInput class
                    // aren't available until API 22.
                    // These classes use reflection to provide support for these classes safely.
                    ActionWrapper.BuilderWrapper actionBuilder = new ActionWrapper.BuilderWrapper(iconId, action.getTitle(), contentIntent);
                    if (action.getTextInputPlaceholder() != null && RemoteInputWrapper.isSupported()) {
                        RemoteInputWrapper.BuilderWrapper remoteInputBuilder = new RemoteInputWrapper.BuilderWrapper(action.getId()+"$Result");
                        remoteInputBuilder.setLabel(action.getTextInputPlaceholder());

                        RemoteInputWrapper remoteInput = remoteInputBuilder.build();
                        actionBuilder.addRemoteInput(remoteInput);
                    }
                    ActionWrapper actionWrapper = actionBuilder.build();
                    new NotificationCompatWrapper.BuilderWrapper(builder).addAction(actionWrapper);
                } else {
                    builder.addAction(iconId, action.getTitle(), contentIntent);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
    }
}
