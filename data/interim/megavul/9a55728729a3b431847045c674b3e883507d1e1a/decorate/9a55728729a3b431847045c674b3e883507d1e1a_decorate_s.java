class decorate {
protected InboxUserNotificationEntity decorate(final SILVERMAILMessage notification) {
    this.id = notification.getId();
    this.source = notification.getSource();
    this.subject = notification.getSubject();
    this.senderName = notification.getSenderName();
    this.date = toLocalDate(notification.getDate()).toString();
    try {
      this.resourceViewUrl = UriBuilder.fromUri(notification.getUrl()).build();
    } catch (Exception e) {
      SilverLogger.getLogger(this).warn(e);
    }
    this.content = notification.getBody();
    this.read = notification.getReaden() > 0;
    return this;
  }
}
