class handleCloseEvent {
protected void handleCloseEvent(final AjaxRequestTarget target)
  {
    csrfTokenHandler.onSubmit();
  }
}
