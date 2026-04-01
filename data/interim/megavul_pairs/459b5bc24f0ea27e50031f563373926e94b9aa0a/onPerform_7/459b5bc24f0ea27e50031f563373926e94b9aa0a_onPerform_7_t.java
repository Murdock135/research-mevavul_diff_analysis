class onPerform_7 {
@Override
    public void onPerform(CommandEvent commandEvent) {
        if (!commandEvent.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            commandEvent.reply("You do not have the permission to do that!");
            return;
        }

        if (commandEvent.isSlashCommand()) {
            OptionMapping optionMapping = commandEvent.getSlashCommandInteractionEvent().getOption("bot");
            commandEvent.getArguments()[0] = optionMapping.getAsString();
        }

        if (commandEvent.getArguments().length == 1) {
            switch (commandEvent.getArguments()[0]) {
                case "mee6" -> importFromMee6(commandEvent);

                default -> commandEvent.reply("Unknown Bot!", 5);
            }
        } else {
            commandEvent.reply("Please provide a Bot you which to Import data from!", 5);
        }
    }
}
