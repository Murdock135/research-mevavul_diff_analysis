class execute_4 {
@Override
	public Controller execute(FolderComponent fc, UserRequest ureq, WindowControl wContr, Translator trans) {
		this.translator = trans;
		this.folderComponent = fc;
		this.fileSelection = new FileSelection(ureq, fc.getCurrentContainer(), fc.getCurrentContainerPath());

		VFSContainer currentContainer = folderComponent.getCurrentContainer();
		List<String> lockedFiles = hasLockedFiles(currentContainer, fileSelection);
		if (lockedFiles.isEmpty()) {
			String msg = trans.translate("del.confirm") + "<p>" + fileSelection.renderAsHtml() + "</p>";		
			// create dialog controller
			dialogCtr = activateYesNoDialog(ureq, trans.translate("del.header"), msg, dialogCtr);
		} else {
			String msg = FolderCommandHelper.renderLockedMessageAsHtml(trans, lockedFiles);
			List<String> buttonLabels = Collections.singletonList(trans.translate("ok"));
			lockedFiledCtr = activateGenericDialog(ureq, trans.translate("lock.title"), msg, buttonLabels, lockedFiledCtr);
		}
		return this;
	}
}
