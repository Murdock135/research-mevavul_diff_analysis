class onSubmit_1 {
@Override
  protected void onSubmit()
  {
    super.onSubmit();
    csrfTokenHandler.onSubmit();
    parentPage.refresh();
  }
}
