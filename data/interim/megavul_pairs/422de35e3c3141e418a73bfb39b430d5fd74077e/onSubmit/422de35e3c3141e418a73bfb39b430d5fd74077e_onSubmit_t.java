class onSubmit {
public void onSubmit()
  {
    final String sessionCsrfToken = getCsrfSessionToken();
    if (StringUtils.equals(sessionCsrfToken, csrfToken) == false) {
      log.error("Cross site request forgery alert. csrf token doesn't match! session csrf token="
          + sessionCsrfToken
          + ", posted csrf token="
          + csrfToken);
      throw new InternalErrorException("errorpage.csrfError");
    }
  }
}
