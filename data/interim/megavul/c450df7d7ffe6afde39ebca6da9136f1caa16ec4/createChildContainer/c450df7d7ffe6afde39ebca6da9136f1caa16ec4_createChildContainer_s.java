class createChildContainer {
@Override
	public VFSContainer createChildContainer(String name) {
		File fNewFile = new File(getBasefile(), name);
		if (!fNewFile.mkdir()) return null;
		LocalFolderImpl locFI =  new LocalFolderImpl(fNewFile, this);
		locFI.setDefaultItemFilter(defaultFilter);
		return locFI;
	}
}
