class dashboard {
@ResponseBody
    public String dashboard(HttpServletRequest request, HttpServletResponse response) {
        return renderAfterAuthentication(request, response, dashboardHTML);
    }
}
