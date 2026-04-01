class getNotificationContext {
private NotificationContext getNotificationContext(final HttpRequest request) {
    final NotificationContext context = new NotificationContext(getCurrentRequester());
    Enumeration<String> parameters = request.getParameterNames();
    while (parameters.hasMoreElements()) {
      final String name = parameters.nextElement();
      context.put(name, request.getParameter(name));
    }
    return context;
  }
}
