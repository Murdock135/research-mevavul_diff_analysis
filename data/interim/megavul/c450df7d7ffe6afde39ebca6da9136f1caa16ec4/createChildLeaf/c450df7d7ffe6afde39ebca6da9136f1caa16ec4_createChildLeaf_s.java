class createChildLeaf {
@Override
	public VFSLeaf createChildLeaf(String name) {
		File fNewFile = new File(getBasefile(), name);
		try {
			if(!fNewFile.getParentFile().exists()) {
				fNewFile.getParentFile().mkdirs();
			}
			if (!fNewFile.createNewFile()) {
				log.warn("Could not create a new leaf::" + name + " in container::" + getBasefile().getAbsolutePath() + " - file alreay exists");
				return null;
			} 
		} catch (Exception e) {
			log.error("Error while creating child leaf::" + name + " in container::" + getBasefile().getAbsolutePath(), e);
			return null;
		}
		return new LocalFileImpl(fNewFile, this);
	}
}
