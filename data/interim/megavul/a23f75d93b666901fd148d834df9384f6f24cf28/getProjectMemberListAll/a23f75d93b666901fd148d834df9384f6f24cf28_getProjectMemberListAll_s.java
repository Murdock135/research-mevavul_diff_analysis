class getProjectMemberListAll {
@GetMapping("/project/member/list")
    public List<User> getProjectMemberListAll() {
        QueryMemberRequest request = new QueryMemberRequest();
        request.setProjectId(SessionUtils.getCurrentProjectId());
        return baseUserService.getProjectMemberList(request);
    }
}
