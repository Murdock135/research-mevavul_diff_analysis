class execute_2 {
public Controller execute(FolderComponent folderComponent, UserRequest ureq, WindowControl wControl, Translator translator) {
		VFSContainer currentContainer = folderComponent.getCurrentContainer();
		VFSContainer rootContainer = folderComponent.getRootContainer();

		if (!VFSManager.exists(currentContainer)) {
			status = FolderCommandStatus.STATUS_FAILED;
			showError(translator.translate("FileDoesNotExist"));
			return null;
		}
		status = FolderCommandHelper.sanityCheck(wControl, folderComponent);
		if (status == FolderCommandStatus.STATUS_FAILED) {
			return null;
		}
		FileSelection selection = new FileSelection(ureq, folderComponent.getCurrentContainer(), folderComponent.getCurrentContainerPath());
		status = FolderCommandHelper.sanityCheck3(wControl, folderComponent, selection);
		if (status == FolderCommandStatus.STATUS_FAILED) {
			return null;
		}

		boolean selectionWithContainer = false;
		List<String> filenames = selection.getFiles();
		List<VFSLeaf> leafs = new ArrayList<>();
		for (String file : filenames) {
			VFSItem item = currentContainer.resolve(file);
			if (item instanceof VFSContainer) {
				selectionWithContainer = true;
			} else if (item instanceof VFSLeaf) {
				leafs.add((VFSLeaf) item);
			}
		}
		if (selectionWithContainer) {
			if (leafs.isEmpty()) {
				wControl.setError(getTranslator().translate("send.mail.noFileSelected"));
				return null;
			} else {
				setFormWarning(getTranslator().translate("send.mail.selectionContainsFolder"));
			}
		}
		setFiles(rootContainer, leafs);
		return this;
	}
}
