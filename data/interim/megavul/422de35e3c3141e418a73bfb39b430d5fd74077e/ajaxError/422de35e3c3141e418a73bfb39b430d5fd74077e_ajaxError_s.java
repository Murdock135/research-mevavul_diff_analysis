class ajaxError {
protected void ajaxError(final String error, final AjaxRequestTarget target)
  {
    form.error(error);
    target.add(formFeedback);
  }
}
