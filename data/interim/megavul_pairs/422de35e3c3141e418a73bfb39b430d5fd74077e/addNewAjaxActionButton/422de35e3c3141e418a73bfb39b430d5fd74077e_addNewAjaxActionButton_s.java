class addNewAjaxActionButton {
private SingleButtonPanel addNewAjaxActionButton(final AjaxCallback ajaxCallback, final String label, final String... classnames)
  {
    final AjaxButton button = new AjaxButton("button", form) {
      private static final long serialVersionUID = -5306532706450731336L;

      @Override
      protected void onSubmit(final AjaxRequestTarget target, final Form< ? > form)
      {
        ajaxCallback.callback(target);
      }

      @Override
      protected void onError(final AjaxRequestTarget target, final Form< ? > form)
      {
        if (ajaxCallback instanceof AjaxFormSubmitCallback) {
          ((AjaxFormSubmitCallback) ajaxCallback).onError(target, form);
        }
      }
    };
    final SingleButtonPanel buttonPanel = new SingleButtonPanel(this.actionButtons.newChildId(), button, label, classnames);
    buttonPanel.add(button);
    return buttonPanel;
  }
}
