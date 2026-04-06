class dashboardData {
@ResponseBody
    public String dashboardData(HttpServletRequest request, HttpServletResponse response) {
        return renderAfterAuthentication(request, response, dashboardJSON);
    }
}
