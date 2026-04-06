class onPerform_1 {
@Override
    public void onPerform(CommandEvent commandEvent) {
        if (!commandEvent.getGuild().getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            Main.getInstance().getCommandManager().sendMessage("I need the permission `Manage Webhooks` to use this command!", commandEvent.getChannel(), commandEvent.getInteractionHook());
        }

        if (commandEvent.isSlashCommand()) {
            Main.getInstance().getCommandManager().sendMessage("This Command doesn't support slash commands yet.", commandEvent.getChannel(), commandEvent.getInteractionHook());
            return;
        }

        if(commandEvent.getArguments().length == 1) {
            if(commandEvent.getArguments()[0].equalsIgnoreCase("list")) {
                StringBuilder end = new StringBuilder("```\n");

                for(String users : Main.getInstance().getSqlConnector().getSqlWorker().getAllYouTubeChannels(commandEvent.getGuild().getId())) {
                    end.append(users).append("\n");
                }

                end.append("```");

                Main.getInstance().getCommandManager().sendMessage(end.toString(), 10, commandEvent.getChannel(), commandEvent.getInteractionHook());

            } else {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "youtubenotifier list/add/remove", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
            }
        } else if(commandEvent.getArguments().length == 3) {

            if (commandEvent.getMessage().getMentions().getChannels(TextChannel.class).isEmpty()) {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "youtubenotifier add/remove YouTubeChannel #Channel", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
                return;
            }

            String name = commandEvent.getArguments()[1];
            if (commandEvent.getArguments()[0].equalsIgnoreCase("add")) {
                commandEvent.getMessage().getMentions().getChannels(TextChannel.class).get(0).createWebhook("Ree6-YouTubeNotifier-" + name).queue(w -> Main.getInstance().getSqlConnector().getSqlWorker().addYouTubeWebhook(commandEvent.getGuild().getId(), w.getId(), w.getToken(), name));
                Main.getInstance().getCommandManager().sendMessage("A YouTube Notifier has been created for the User " + name + "!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());

                if (!Main.getInstance().getNotifier().isYouTubeRegistered(name)) {
                    Main.getInstance().getNotifier().registerYouTubeChannel(name);
                }
            } else if (commandEvent.getArguments()[0].equalsIgnoreCase("remove")) {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "youtubenotifier remove YouTubeChannel", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
            } else {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "youtubenotifier add YouTubeChannel #Channel", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
            }
        } else if(commandEvent.getArguments().length == 2) {
            String name = commandEvent.getArguments()[1];
            if(commandEvent.getArguments()[0].equalsIgnoreCase("remove")) {
                Main.getInstance().getSqlConnector().getSqlWorker().removeYouTubeWebhook(commandEvent.getGuild().getId(), name);
                Main.getInstance().getCommandManager().sendMessage("A YouTube Notifier has been removed from the User " + name + "!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());

                if (Main.getInstance().getNotifier().isYouTubeRegistered(name)) {
                    Main.getInstance().getNotifier().unregisterYouTubeChannel(name);
                }
            } else if (commandEvent.getArguments()[0].equalsIgnoreCase("add")) {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "youtubenotifier add YouTubeChannel #Channel", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
            } else {
                Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "youtubenotifier remove YouTubeChannel", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
            }
        } else {
            Main.getInstance().getCommandManager().sendMessage("Please use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "youtubenotifier list/add/remove", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
        }
        Main.getInstance().getCommandManager().deleteMessage(commandEvent.getMessage(), commandEvent.getInteractionHook());
    }
}
