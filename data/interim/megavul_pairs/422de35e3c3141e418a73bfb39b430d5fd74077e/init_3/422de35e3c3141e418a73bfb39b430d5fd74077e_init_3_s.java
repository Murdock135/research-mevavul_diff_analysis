class init_3 {
@SuppressWarnings("serial")
  protected void init(final Form< ? > form)
  {
    this.form = form;
    mainSubContainer.add(form);
    form.add(gridContentContainer);
    form.add(buttonBarContainer);
    if (showCancelButton == true) {
      final SingleButtonPanel cancelButton = appendNewAjaxActionButton(new AjaxCallback() {
        @Override
        public void callback(final AjaxRequestTarget target)
        {
          onCancelButtonSubmit(target);
          close(target);
        }
      }, getString("cancel"), SingleButtonPanel.CANCEL);
      cancelButton.getButton().setDefaultFormProcessing(false);
    }
    closeButtonPanel = appendNewAjaxActionButton(new AjaxFormSubmitCallback() {

      @Override
      public void callback(final AjaxRequestTarget target)
      {
        if (onCloseButtonSubmit(target)) {
          close(target);
        }
      }

      @Override
      public void onError(final AjaxRequestTarget target, final Form< ? > form)
      {
        ModalDialog.this.onError(target, form);
      }
    }, closeButtonLabel != null ? closeButtonLabel : getString("close"), SingleButtonPanel.NORMAL);
    buttonBarContainer.add(actionButtons.getRepeatingView());
    form.setDefaultButton(closeButtonPanel.getButton());
    if (autoGenerateGridBuilder == true) {
      gridBuilder = new GridBuilder(gridContentContainer, "flowform");
    }
    initFeedback(gridContentContainer);
  }
}
