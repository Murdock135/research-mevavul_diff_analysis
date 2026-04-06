class dashboard {
@RequestMapping(value = "/add-on/business-continuity/admin/dashboard", method = RequestMethod.GET)
    @ResponseBody
    public String dashboard(HttpServletRequest request, HttpServletResponse response) {
        return renderAfterAuthentication(request, response, dashboardHTML);
    }
}
