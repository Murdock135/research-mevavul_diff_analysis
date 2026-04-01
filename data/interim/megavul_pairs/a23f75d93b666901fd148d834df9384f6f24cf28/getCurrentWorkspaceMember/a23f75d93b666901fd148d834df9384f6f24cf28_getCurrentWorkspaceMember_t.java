class getCurrentWorkspaceMember {
@GetMapping("/ws/current/member/list")
    public List<User> getCurrentWorkspaceMember() {
        SessionUser user = SessionUtils.getUser();
        Optional<UserGroup> any = user.getUserGroups().stream()
                .filter(ug -> (ug.getSourceId().equals(SessionUtils.getCurrentWorkspaceId()) || ug.getGroupId().equals(UserGroupConstants.SUPER_GROUP)))
                .findAny();
        if (any.isEmpty()) {
            return new ArrayList<>();
        }
        QueryMemberRequest request = new QueryMemberRequest();
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return baseUserService.getMemberList(request);
    }
}
