class cleanupRestoredProject {
void cleanupRestoredProject(ProjectLocator projectLocator) {

		Project project = tool.getProject();
		ProjectManager projectManager = project.getProjectManager();

		// delete the project at the given project location
		if (!projectManager.deleteProject(projectLocator)) {
			Msg.showError(this, null, "All Files in Project not Removed",
				"Not all files have been deleted from project " + projectLocator.getName());
		}
	}
}
