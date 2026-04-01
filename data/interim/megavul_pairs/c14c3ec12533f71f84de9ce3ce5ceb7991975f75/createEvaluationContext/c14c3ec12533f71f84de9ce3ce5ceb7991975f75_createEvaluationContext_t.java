class createEvaluationContext {
protected EvaluationContext createEvaluationContext(InstanceEvent event, Instance instance) {
		Map<String, Object> root = new HashMap<>();
		root.put("event", event);
		root.put("instance", instance);
		root.put("lastStatus", getLastStatus(event.getInstance()));
		return SimpleEvaluationContext
				.forPropertyAccessors(DataBindingPropertyAccessor.forReadOnlyAccess(), new MapAccessor())
				.withRootObject(root).build();
	}
}
