class wantsNotificationOnClose {
@SuppressWarnings("serial")
  public ModalDialog wantsNotificationOnClose()
  {
    mainContainer.add(new AjaxEventBehavior("hidden") {
      @Override
      protected void onEvent(final AjaxRequestTarget target)
      {
        handleCloseEvent(target);
      }
    });
    return this;
  }
}
