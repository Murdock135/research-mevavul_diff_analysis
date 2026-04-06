class createChildContainer {
@Override
	public VFSContainer createChildContainer(String name) {
		File fNewFile = new File(getBasefile(), name);
		if(!isInPath(name)) {
			log.warn("Could not create a new container::{} in container::{} - file out of parent directory", name, getBasefile().getAbsolutePath());
			return null;
		}
		if (!fNewFile.mkdir()) {
			return null;
		}
		LocalFolderImpl locFI =  new LocalFolderImpl(fNewFile, this);
		locFI.setDefaultItemFilter(defaultFilter);
		return locFI;
	}
}
