class getNotificationContext {
private NotificationContext getNotificationContext(final HttpRequest request) {
    final NotificationContext context = new NotificationContext(getCurrentRequester());
    Enumeration<String> parameters = request.getParameterNames();
    final HtmlSanitizer htmlSanitizer = HtmlSanitizer.get();
    while (parameters.hasMoreElements()) {
      final String name = parameters.nextElement();
      context.put(name, htmlSanitizer.sanitize(request.getParameter(name)));
    }
    return context;
  }
}
