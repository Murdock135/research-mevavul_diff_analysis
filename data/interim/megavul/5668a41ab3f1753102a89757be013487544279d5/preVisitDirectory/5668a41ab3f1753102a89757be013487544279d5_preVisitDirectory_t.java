class preVisitDirectory {
@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
	throws IOException {
		Path relativeDir = source.relativize(dir);
		final Path dirToCreate = Paths.get(destDir.toString(), relativeDir.toString());
		checkPath(dirToCreate);
       
        if(!dirToCreate.toFile().exists()) {
        	Files.createDirectory(dirToCreate);
        }
        return FileVisitResult.CONTINUE;
	}
}
