class loginRender {
private void loginRender(Controller controller) throws MalformedURLException {
        HttpServletRequest request = controller.getRequest();
        String url = controller.getRequest().getRequestURL().toString();
        URL tUrl = new URL(url);
        AdminPageController.previewField(controller);
        controller.getRequest().setAttribute("redirectFrom", tUrl.getPath() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        controller.render(new FreeMarkerRender("/admin/login.ftl"));

    }
}
