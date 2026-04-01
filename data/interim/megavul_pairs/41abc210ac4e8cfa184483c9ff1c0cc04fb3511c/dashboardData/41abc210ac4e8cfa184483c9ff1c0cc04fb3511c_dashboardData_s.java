class dashboardData {
@RequestMapping(value = "/add-on/business-continuity/admin/dashboard.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String dashboardData(HttpServletRequest request, HttpServletResponse response) {
        return renderAfterAuthentication(request, response, dashboardJSON);
    }
}
