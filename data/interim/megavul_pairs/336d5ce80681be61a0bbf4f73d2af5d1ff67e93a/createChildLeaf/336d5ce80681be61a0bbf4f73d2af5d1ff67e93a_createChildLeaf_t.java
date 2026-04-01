class createChildLeaf {
@Override
	public VFSLeaf createChildLeaf(String name) {
		name = cleanFilename(name); // backward compatibility
		File fNewFile = new File(getBasefile(), name);
		try {
			if(!isInPath(name)) {
				log.warn("Could not create a new leaf::{} in container::{} - file out of parent directory", name, getBasefile().getAbsolutePath());
				return null;
			}
			if(!fNewFile.getParentFile().exists()) {
				fNewFile.getParentFile().mkdirs();
			}
			if (!fNewFile.createNewFile()) {
				log.warn("Could not create a new leaf::{} in container::{} - file alreay exists", name, getBasefile().getAbsolutePath());
				return null;
			} 
		} catch (Exception e) {
			log.error("Error while creating child leaf::{} in container::{}", name, getBasefile().getAbsolutePath(), e);
			return null;
		}
		return new LocalFileImpl(fNewFile, this);
	}
}
