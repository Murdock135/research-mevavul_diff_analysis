class close {
public void close(final AjaxRequestTarget target)
  {
    target.appendJavaScript("$('#" + getMainContainerMarkupId() + "').modal('hide');");
  }
}
