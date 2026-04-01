class addProjectPages {
private void addProjectPages() {
		add(new DynamicPathPageMapper("projects", ProjectListPage.class));
		add(new DynamicPathPageMapper("projects/new", NewProjectPage.class));
		add(new DynamicPathPageMapper("projects/${project}", ProjectDashboardPage.class));

		add(new DynamicPathPageMapper("projects/${project}/blob/#{revision}/#{path}", ProjectBlobPage.class));
		add(new DynamicPathPageMapper("projects/${project}/commits", ProjectCommitsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/commits/${revision}", CommitDetailPage.class));
		add(new DynamicPathPageMapper("projects/${project}/compare", RevisionComparePage.class));
		add(new DynamicPathPageMapper("projects/${project}/stats/contribs", ProjectContribsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/stats/lines", SourceLinesPage.class));

		add(new DynamicPathPageMapper("projects/${project}/branches", ProjectBranchesPage.class));
		add(new DynamicPathPageMapper("projects/${project}/tags", ProjectTagsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/code-comments", ProjectCodeCommentsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/code-comments/${code-comment}/invalid", InvalidCodeCommentPage.class));

		add(new DynamicPathPageMapper("projects/${project}/pulls", ProjectPullRequestsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/pulls/new", NewPullRequestPage.class));
		add(new DynamicPathPageMapper("projects/${project}/pulls/${request}", PullRequestActivitiesPage.class));
		add(new DynamicPathPageMapper("projects/${project}/pulls/${request}/activities", PullRequestActivitiesPage.class));
		add(new DynamicPathPageMapper("projects/${project}/pulls/${request}/code-comments", PullRequestCodeCommentsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/pulls/${request}/changes", PullRequestChangesPage.class));
		add(new DynamicPathPageMapper("projects/${project}/pulls/${request}/merge-preview", MergePreviewPage.class));
		add(new DynamicPathPageMapper("projects/${project}/pulls/${request}/invalid", InvalidPullRequestPage.class));

		add(new DynamicPathPageMapper("projects/${project}/issues/boards", IssueBoardsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/issues/boards/${board}", IssueBoardsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/issues/list", ProjectIssueListPage.class));
		add(new DynamicPathPageMapper("projects/${project}/issues/${issue}", IssueActivitiesPage.class));
		add(new DynamicPathPageMapper("projects/${project}/issues/${issue}/activities", IssueActivitiesPage.class));
		add(new DynamicPathPageMapper("projects/${project}/issues/${issue}/commits", IssueCommitsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/issues/${issue}/pull-requests", IssuePullRequestsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/issues/${issue}/builds", IssueBuildsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/issues/new", NewIssuePage.class));
		add(new DynamicPathPageMapper("projects/${project}/milestones", MilestoneListPage.class));
		add(new DynamicPathPageMapper("projects/${project}/milestones/${milestone}", MilestoneDetailPage.class));
		add(new DynamicPathPageMapper("projects/${project}/milestones/${milestone}/edit", MilestoneEditPage.class));
		add(new DynamicPathPageMapper("projects/${project}/milestones/new", NewMilestonePage.class));
		
		add(new DynamicPathPageMapper("projects/${project}/builds", ProjectBuildsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/builds/${build}", BuildDashboardPage.class));
		add(new DynamicPathPageMapper("projects/${project}/builds/${build}/log", BuildLogPage.class));
		add(new DynamicPathPageMapper("projects/${project}/builds/${build}/changes", BuildChangesPage.class));
		add(new DynamicPathPageMapper("projects/${project}/builds/${build}/fixed-issues", FixedIssuesPage.class));
		add(new DynamicPathPageMapper("projects/${project}/builds/${build}/artifacts", BuildArtifactsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/builds/${build}/invalid", InvalidBuildPage.class));
		
		add(new DynamicPathPageMapper("projects/${project}/settings/general", GeneralSecuritySettingPage.class));
		add(new DynamicPathPageMapper("projects/${project}/settings/authorizations", ProjectAuthorizationsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/settings/avatar-edit", AvatarEditPage.class));
		add(new DynamicPathPageMapper("projects/${project}/settings/branch-protection", BranchProtectionsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/settings/tag-protection", TagProtectionsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/settings/build/job-secrets", JobSecretsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/settings/build/action-authorizations", ActionAuthorizationsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/settings/build/build-preserve-rules", BuildPreservationsPage.class));
		add(new DynamicPathPageMapper("projects/${project}/settings/web-hooks", WebHooksPage.class));
	}
}
