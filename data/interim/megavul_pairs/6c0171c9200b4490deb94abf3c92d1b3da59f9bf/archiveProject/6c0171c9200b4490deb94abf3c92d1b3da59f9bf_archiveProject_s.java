class archiveProject {
private void archiveProject() {

		FrontEndTool feTool = (FrontEndTool) tool;
		Project activeProject = AppInfo.getActiveProject();
		if (activeProject.getToolManager().getRunningTools().length > 0) {
			Msg.showInfo(getClass(), tool.getToolFrame(), TOOL_RUNNING_TITLE,
					"You must close running tools before starting the archive process.");
			return;
		}

		activeProject.saveToolTemplate("FRONTEND", feTool.saveToolToToolTemplate());
		activeProject.save();

		if (archiveDialog == null) {
			archiveDialog = new ArchiveDialog(this);
		}

		ProjectLocator projectLocator = activeProject.getProjectLocator();
		String archivePathName = getArchivePathName(projectLocator);
		if (!archiveDialog.showDialog(projectLocator, archivePathName, tool)) {
			return;
		}

		archivePathName = archiveDialog.getArchivePathName();

		File archiveJar = new File(archivePathName);
		File parentFile = archiveJar.getParentFile();
		Preferences.setProperty(LAST_ARCHIVE_DIR, parentFile.getAbsolutePath());

		isArchiving = true;
		archivingListener = new TaskListener() {
			@Override
			public void taskCompleted(Task task) {
				isArchiving = false;
			}

			@Override
			public void taskCancelled(Task task) {
				isArchiving = false;
			}
		};

		Task task = new ArchiveTask(activeProject, archiveJar);
		task.addTaskListener(archivingListener);
		new TaskLauncher(task, tool.getToolFrame());
	}
}
