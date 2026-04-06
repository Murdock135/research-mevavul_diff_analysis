class execute_3 {
@Override
	public Controller execute(FolderComponent folderComponent, UserRequest ureq, WindowControl wControl, Translator trans) {
		setTranslator(trans);
		currentContainer = folderComponent.getCurrentContainer();
		if (currentContainer.canWrite() != VFSConstants.YES) {
			throw new AssertException("Cannot write to current folder.");
		}
		
		status = FolderCommandHelper.sanityCheck(wControl, folderComponent);
		if(status == FolderCommandStatus.STATUS_FAILED) {
			return null;
		}
	
		selection = new FileSelection(ureq, folderComponent.getCurrentContainerPath());
		status = FolderCommandHelper.sanityCheck3(wControl, folderComponent, selection);
		if(status == FolderCommandStatus.STATUS_FAILED) {
			return null;
		}
		
		if(selection.getFiles().isEmpty()) {
			status = FolderCommandStatus.STATUS_FAILED;
			wControl.setWarning(trans.translate("warning.file.selection.empty"));
			return null;
		}
		
		initForm(ureq);
		return this;
	}
}
