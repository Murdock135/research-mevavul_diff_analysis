class sendDefaultChoice {
public void sendDefaultChoice(SelectMenuInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder(event.getMessage().getEmbeds().get(0));

        List<SelectOption> optionList = new ArrayList<>();
        optionList.add(SelectOption.of("Audit-Logging", "log"));
        optionList.add(SelectOption.of("Welcome-channel", "welcome"));
        optionList.add(SelectOption.of("Autorole", "autorole"));
        optionList.add(SelectOption.of("Temporal-Voice", "tempvoice"));
        optionList.add(SelectOption.of("Statistics", "statistics"));

        embedBuilder.setDescription("Which configuration do you want to check out?");

        event.editMessageEmbeds(embedBuilder.build()).setActionRow(new SelectMenuImpl("setupActionMenu", "Select a configuration Step!", 1, 1, false, optionList)).queue();
    }
}
