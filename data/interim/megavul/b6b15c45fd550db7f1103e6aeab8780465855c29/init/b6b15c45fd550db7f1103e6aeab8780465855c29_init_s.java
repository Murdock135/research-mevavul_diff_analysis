class init {
public void init(Object obj) {
		ViewContext context = (ViewContext) obj;
		this.request = context.getRequest();
		ctx = context.getVelocityContext();
		this.hostWebAPI = WebAPILocator.getHostWebAPI();
	}
}
