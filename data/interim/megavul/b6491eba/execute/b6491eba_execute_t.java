class execute {
@Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm formIn,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        RequestContext context = new RequestContext(request);
        ManagedServerGroup serverGroup = context.lookupAndBindServerGroup();
        if (context.isSubmitted()) {
            ServerGroupManager manager = ServerGroupManager.getInstance();
            manager.remove(context.getCurrentUser(), serverGroup);
            String [] params = {StringEscapeUtils.escapeHtml(serverGroup.getName())};
            getStrutsDelegate().saveMessage(DELETED_MESSAGE_KEY, params, request);
            return mapping.findForward("success");
        }
        return mapping.findForward(RhnHelper.DEFAULT_FORWARD);
    }
}
