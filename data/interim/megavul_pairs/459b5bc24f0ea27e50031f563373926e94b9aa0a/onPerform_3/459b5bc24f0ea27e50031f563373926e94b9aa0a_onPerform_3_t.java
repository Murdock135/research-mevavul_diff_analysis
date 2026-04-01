class onPerform_3 {
@Override
    public void onPerform(CommandEvent commandEvent) {

        if (commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR) && commandEvent.getMember().hasPermission(Permission.MANAGE_SERVER)) {

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Setup Menu")
                    .setFooter(commandEvent.getGuild().getName() + " - " + Data.ADVERTISEMENT, commandEvent.getGuild().getIconUrl())
                    .setColor(Color.cyan)
                    .setDescription("Which configuration do you want to check out?");

            List<SelectOption> optionList = new ArrayList<>();
            optionList.add(SelectOption.of("Audit-Logging", "log"));
            optionList.add(SelectOption.of("Welcome-channel", "welcome"));
            optionList.add(SelectOption.of("Autorole", "autorole"));
            optionList.add(SelectOption.of("Temporal-Voice", "tempvoice"));
            optionList.add(SelectOption.of("Statistics", "statistics"));

            SelectMenu selectMenu = new SelectMenuImpl("setupActionMenu", "Select a configuration Step!", 1, 1, false, optionList);

            if (commandEvent.isSlashCommand()) {
                commandEvent.getInteractionHook().sendMessageEmbeds(embedBuilder.build())
                        .addActionRow(selectMenu).queue();
            } else {
                commandEvent.getChannel().sendMessageEmbeds(embedBuilder.build())
                        .addActionRow(selectMenu).queue();
            }
        } else {
            Main.getInstance().getCommandManager().sendMessage("You dont have the Permission for this Command!", 5, commandEvent.getChannel(), commandEvent.getInteractionHook());
        }
        Main.getInstance().getCommandManager().deleteMessage(commandEvent.getMessage(), commandEvent.getInteractionHook());
    }
}
