class unzip {
private static boolean unzip(InputStream in, VFSContainer targetDir, Identity identity, boolean versioning) {
		
		VFSRepositoryService vfsRepositoryService = CoreSpringFactory.getImpl(VFSRepositoryService.class);

		try(ZipInputStream oZip = new ZipInputStream(in)) {
			// unzip files
			ZipEntry oEntr = oZip.getNextEntry();
			while (oEntr != null) {
				String name = oEntr.getName();
				if(!targetDir.isInPath(name)) {
					throw new IOException("Invalip ZIP");
				}

				if (name != null && !name.startsWith(DIR_NAME__MACOSX)) {
					if (oEntr.isDirectory()) {
						// skip MacOSX specific metadata directory
						// create directories
						getAllSubdirs(targetDir, name, identity, true);
					} else {
						// create file
						VFSContainer createIn = targetDir;
						// check if entry has directories which did not show up as
						// directories above
						int dirSepIndex = name.lastIndexOf('/');
						if (dirSepIndex == -1) {
							// try it windows style, backslash is also valid format
							dirSepIndex = name.lastIndexOf('\\');
						}
						if (dirSepIndex > 0) {
							// create subdirs
							createIn = getAllSubdirs(targetDir, name.substring(0, dirSepIndex), identity, true);
							if (createIn == null) {
								log.debug("Error creating directory structure for zip entry: {}", oEntr.getName());
								return false;
							}
							name = name.substring(dirSepIndex + 1);
						}
						
						if(versioning) {
							VFSLeaf newEntry = (VFSLeaf)createIn.resolve(name);
							if(newEntry == null) {
								newEntry = createIn.createChildLeaf(name);
								if (!copy(oZip, newEntry)) {
									return false;
								}
							} else if (newEntry.canVersion() == VFSConstants.YES) {
								vfsRepositoryService.addVersion(newEntry, identity, "", oZip);
							}
							if(newEntry != null && identity != null && newEntry.canMeta() == VFSConstants.YES) {
								VFSMetadata info = newEntry.getMetaInfo();
								if(info != null) {
									info.setAuthor(identity);
									vfsRepositoryService.updateMetadata(info);
								}
							}
							
						} else {
							VFSLeaf newEntry = createIn.createChildLeaf(name);
							if (newEntry != null) {
								if (!copy(oZip, newEntry)) {
									return false;
								}
					
								if(identity != null && newEntry.canMeta() == VFSConstants.YES) {
									VFSMetadata info = newEntry.getMetaInfo();
									if(info != null) {
										info.setAuthor(identity);
										vfsRepositoryService.updateMetadata(info);
									}
								}
							}
						}
					}
				}
				oZip.closeEntry();
				oEntr = oZip.getNextEntry();
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
