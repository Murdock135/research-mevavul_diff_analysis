class onSubmit {
public void onSubmit()
  {
    final String sessionCsrfToken = getCsrfSessionToken();
    final String postedCsrfToken = this.csrfTokenField.getInput();
    if (StringUtils.equals(sessionCsrfToken, postedCsrfToken) == false) {
      log.error("Cross site request forgery alert. csrf token doesn't match! session csrf token="
          + sessionCsrfToken
          + ", posted csrf token="
          + postedCsrfToken);
      throw new InternalErrorException("errorpage.csrfError");
    }
  }
}
