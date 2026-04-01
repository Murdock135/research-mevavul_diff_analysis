class doFilter_1 {
@Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws
      IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) resp;

    String token = request.getHeader("Authorization");

    Long consumerId = consumerAuthUtil.getConsumerId(token);

    if (consumerId == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }

    consumerAuthUtil.storeConsumerId(request, consumerId);
    consumerAuditUtil.audit(request, consumerId);

    chain.doFilter(req, resp);
  }
}
