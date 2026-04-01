class searchRootDirectory {
protected static  RootSearcher searchRootDirectory(Path fPath)
	throws IOException {
		RootSearcher rootSearcher = new RootSearcher();
		Files.walkFileTree(fPath, EnumSet.noneOf(FileVisitOption.class), 16, rootSearcher);
		return rootSearcher;
	}
}
