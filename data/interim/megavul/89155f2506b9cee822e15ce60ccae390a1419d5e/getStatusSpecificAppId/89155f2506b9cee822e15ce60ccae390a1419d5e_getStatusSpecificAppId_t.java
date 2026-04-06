class getStatusSpecificAppId {
@RequestMapping(value = "/{appId}" + STATUS_URL + "/{referenceId:\\S+}.json", method = RequestMethod.GET)
    public final void getStatusSpecificAppId(
            @SuppressWarnings("unused") @PathVariable final String appId,
            @PathVariable final String referenceId,
            final HttpServletRequest statusRequest,
            final HttpServletResponse statusResponse) {
        getStatus(referenceId, statusRequest, statusResponse);
    }
}
