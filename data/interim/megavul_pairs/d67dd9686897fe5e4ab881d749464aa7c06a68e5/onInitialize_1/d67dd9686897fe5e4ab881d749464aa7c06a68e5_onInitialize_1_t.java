class onInitialize_1 {
@Override
	protected void onInitialize() {
		super.onInitialize();

		IModel<String> valueModel = new AbstractReadOnlyModel<String>() {

			@Override
			public String getObject() {
				return getUser().getAccessToken();
			}
			
		};
		add(new TextField<String>("value", valueModel) {

			@Override
			protected String[] getInputTypes() {
				return new String[] {"password"};
			}
			
		});
		
		add(new CopyToClipboardLink("copy", valueModel));
		
		add(new Link<Void>("regenerate") {

			@Override
			public void onClick() {
				getUser().setAccessToken(CryptoUtils.generateSecret());
				OneDev.getInstance(UserManager.class).save(getUser());
				Session.get().success("Access token regenerated");
				setResponsePage(getPage());
			}
			
		}.add(new ConfirmClickModifier("This will invalidate current token and generate a new one, do you want to continue?")));
	}
}
