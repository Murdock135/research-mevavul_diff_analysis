class ajaxError {
protected void ajaxError(final String error, final AjaxRequestTarget target)
  {
    csrfTokenHandler.onSubmit();
    form.error(error);
    target.add(formFeedback);
  }
}
