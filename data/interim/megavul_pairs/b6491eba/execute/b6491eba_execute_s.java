class execute {
public ActionForward execute(ActionMapping mapping,
                                 ActionForm formIn,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        RequestContext context = new RequestContext(request);
        ManagedServerGroup serverGroup = context.lookupAndBindServerGroup();
        if (context.isSubmitted()) {
            String [] params = {serverGroup.getName()};
            ServerGroupManager manager = ServerGroupManager.getInstance();
            manager.remove(context.getCurrentUser(), serverGroup);
            getStrutsDelegate().saveMessage(DELETED_MESSAGE_KEY, params, request);
            return mapping.findForward("success");
        }
        return mapping.findForward(RhnHelper.DEFAULT_FORWARD);
    }
}
