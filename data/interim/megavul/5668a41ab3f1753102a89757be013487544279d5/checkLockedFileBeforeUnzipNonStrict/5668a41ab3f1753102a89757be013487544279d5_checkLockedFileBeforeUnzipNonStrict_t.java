class checkLockedFileBeforeUnzipNonStrict {
public static List<String> checkLockedFileBeforeUnzipNonStrict(VFSLeaf zipLeaf, VFSContainer targetDir, Identity identity) {
		List<String> lockedFiles = new ArrayList<>();
		VFSLockManager vfsLockManager = CoreSpringFactory.getImpl(VFSLockManager.class);
		
		try(InputStream in = zipLeaf.getInputStream();
				net.sf.jazzlib.ZipInputStream oZip = new net.sf.jazzlib.ZipInputStream(in);) {
			// unzip files
			net.sf.jazzlib.ZipEntry oEntr = oZip.getNextEntry();
			while (oEntr != null) {
				if (oEntr.getName() != null && !oEntr.getName().startsWith(DIR_NAME__MACOSX)) {
					if (oEntr.isDirectory()) {
						// skip MacOSX specific metadata directory
						// directories aren't locked
						oZip.closeEntry();
						oEntr = oZip.getNextEntry();//TODO zip
						continue;
					} else {
						// search file
						VFSContainer createIn = targetDir;
						String name = oEntr.getName();
						// check if entry has directories which did not show up as
						// directories above
						int dirSepIndex = name.lastIndexOf('/');
						if (dirSepIndex == -1) {
							// try it windows style, backslash is also valid format
							dirSepIndex = name.lastIndexOf('\\');
						}
						if (dirSepIndex > 0) {
							// get subdirs
							createIn = getAllSubdirs(targetDir, name.substring(0, dirSepIndex), identity, false);
							if (createIn == null) {
								//sub directories don't exist, and aren't locked
								oZip.closeEntry();
								oEntr = oZip.getNextEntry();
								continue;
							}
							name = name.substring(dirSepIndex + 1);
						}
						
						VFSLeaf newEntry = (VFSLeaf)createIn.resolve(name);
						if(vfsLockManager.isLockedForMe(newEntry, identity, VFSLockApplicationType.vfs, null)) {
							lockedFiles.add(name);
						}
					}
				}
				oZip.closeEntry();
				oEntr = oZip.getNextEntry();
			}
		} catch (IOException e) {
			return null;
		}

		return lockedFiles;
	}
}
