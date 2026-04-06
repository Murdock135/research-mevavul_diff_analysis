class createMessage {
protected Message createMessage(Instance instance, String registeredTitle, String activitySubtitle,
			EvaluationContext context) {
		List<Fact> facts = new ArrayList<>();
		facts.add(new Fact(STATUS_KEY, instance.getStatusInfo().getStatus()));
		facts.add(new Fact(SERVICE_URL_KEY, instance.getRegistration().getServiceUrl()));
		facts.add(new Fact(HEALTH_URL_KEY, instance.getRegistration().getHealthUrl()));
		facts.add(new Fact(MANAGEMENT_URL_KEY, instance.getRegistration().getManagementUrl()));
		facts.add(new Fact(SOURCE_KEY, instance.getRegistration().getSource()));

		Section section = Section.builder().activityTitle(instance.getRegistration().getName())
				.activitySubtitle(activitySubtitle).facts(facts).build();

		return Message.builder().title(registeredTitle).summary(messageSummary)
				.themeColor(evaluateExpression(context, themeColor)).sections(singletonList(section)).build();
	}
}
