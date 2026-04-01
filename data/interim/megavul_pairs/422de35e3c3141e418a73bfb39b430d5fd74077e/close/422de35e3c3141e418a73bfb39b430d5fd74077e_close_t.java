class close {
public void close(final AjaxRequestTarget target)
  {
    csrfTokenHandler.onSubmit();
    target.appendJavaScript("$('#" + getMainContainerMarkupId() + "').modal('hide');");
  }
}
