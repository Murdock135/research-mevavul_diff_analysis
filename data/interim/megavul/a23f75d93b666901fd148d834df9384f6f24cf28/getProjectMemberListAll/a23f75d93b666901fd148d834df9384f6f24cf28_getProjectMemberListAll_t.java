class getProjectMemberListAll {
@GetMapping("/project/member/list")
    public List<User> getProjectMemberListAll() {
        SessionUser user = SessionUtils.getUser();
        Optional<UserGroup> any = user.getUserGroups().stream()
                .filter(ug -> (ug.getSourceId().equals(SessionUtils.getCurrentProjectId()) || ug.getGroupId().equals(UserGroupConstants.SUPER_GROUP)))
                .findAny();
        if (any.isEmpty()) {
            return new ArrayList<>();
        }
        QueryMemberRequest request = new QueryMemberRequest();
        request.setProjectId(SessionUtils.getCurrentProjectId());
        return baseUserService.getProjectMemberList(request);
    }
}
