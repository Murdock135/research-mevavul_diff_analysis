class execute_1 {
@Override
	public Controller execute(FolderComponent folderComponent, UserRequest ureq, WindowControl wControl, Translator trans) {
		VFSContainer currentContainer = folderComponent.getCurrentContainer();

		status = FolderCommandHelper.sanityCheck(wControl, folderComponent);
		if(status == FolderCommandStatus.STATUS_FAILED) {
			return null;
		}
	
		FileSelection selection = new FileSelection(ureq, folderComponent.getCurrentContainer(), folderComponent.getCurrentContainerPath());
		status = FolderCommandHelper.sanityCheck3(wControl, folderComponent, selection);
		if(status == FolderCommandStatus.STATUS_FAILED) {
			return null;
		}
		
		if(selection.getFiles().isEmpty()) {
			status = FolderCommandStatus.STATUS_FAILED;
			wControl.setWarning(trans.translate("warning.file.selection.empty22"));
			return null;
		}
		
		MediaResource mr = new ZipMediaResource(currentContainer, selection);
		ureq.getDispatchResult().setResultingMediaResource(mr);
		return null;
	}
}
