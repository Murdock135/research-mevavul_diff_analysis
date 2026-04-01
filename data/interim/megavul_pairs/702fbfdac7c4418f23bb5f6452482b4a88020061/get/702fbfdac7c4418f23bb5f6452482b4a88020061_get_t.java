class get {
public void get(PageModel model, @MethodParam("getAccount") Account account,
	                @SpringBean("adminAccountService") AccountService accountService,
	                @SpringBean("adminService") AdministrationService administrationService,
	                @SpringBean("providerManagementService") ProviderManagementService providerManagementService,
					UiUtils uu,
					@SpringBean("appFrameworkService") AppFrameworkService appFrameworkService)
	    throws IOException {
		
		setModelAttributes(model, account, null, accountService, administrationService, providerManagementService, uu, appFrameworkService);
		if (account.getPerson().getPersonId() == null) {
			setJsonFormData(model, account, null, uu);
		}
	}
}
