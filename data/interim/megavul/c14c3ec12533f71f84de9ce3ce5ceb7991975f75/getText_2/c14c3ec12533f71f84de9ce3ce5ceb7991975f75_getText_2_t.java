class getText_2 {
private Object getText(InstanceEvent event, Instance instance) {
		Map<String, Object> root = new HashMap<>();
		root.put("event", event);
		root.put("instance", instance);
		root.put("lastStatus", getLastStatus(event.getInstance()));
		SimpleEvaluationContext context = SimpleEvaluationContext
				.forPropertyAccessors(DataBindingPropertyAccessor.forReadOnlyAccess(), new MapAccessor())
				.withRootObject(root).build();
		return message.getValue(context, String.class);
	}
}
