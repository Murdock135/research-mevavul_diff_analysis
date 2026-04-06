class getCurrentWorkspaceMember {
@GetMapping("/ws/current/member/list")
    public List<User> getCurrentWorkspaceMember() {
        QueryMemberRequest request = new QueryMemberRequest();
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return baseUserService.getMemberList(request);
    }
}
