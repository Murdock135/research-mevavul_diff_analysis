class buildUrl_1 {
protected String buildUrl(InstanceEvent event, Instance instance) {
		if ((event instanceof InstanceStatusChangedEvent)
				&& (StatusInfo.STATUS_UP.equals(((InstanceStatusChangedEvent) event).getStatusInfo().getStatus()))) {
			return String.format("%s/%s/close", url, generateAlias(instance));
		}
		return url.toString();
	}
}
