class getProjectMembers {
@GetMapping("/project/member/{projectId}")
    public List<User> getProjectMembers(@PathVariable String projectId) {
        SessionUser user = SessionUtils.getUser();
        Optional<UserGroup> any = user.getUserGroups().stream()
                .filter(ug -> (ug.getSourceId().equals(projectId) || ug.getGroupId().equals(UserGroupConstants.SUPER_GROUP)))
                .findAny();
        if (any.isEmpty()) {
            return new ArrayList<>();
        }
        QueryMemberRequest request = new QueryMemberRequest();
        request.setProjectId(projectId);
        return baseUserService.getProjectMemberList(request);
    }
}
