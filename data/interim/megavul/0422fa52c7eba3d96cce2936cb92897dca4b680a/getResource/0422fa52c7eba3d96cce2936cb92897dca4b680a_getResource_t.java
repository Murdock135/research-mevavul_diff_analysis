class getResource {
@Override
	public File getResource(String path) {
		if (resourceShortcuts != null && resourceShortcuts.containsKey(path)) {
			path = resourceShortcuts.get(path);
		}

		if (path == null || new File(path).isAbsolute() || !path.equals(FilenameUtils.normalize(path))) {
			return null;
		}
		
		if (developmentFolders != null) {
			for (File developmentFolder : developmentFolders) {
	    		// we're in development mode, and we want to dynamically reload resource from this filesystem directory
				File file = new File(developmentFolder, path);
				if (file.exists()) {
					return file;
				}
			}
			return null;
    	}
    	else {
    		ModuleClassLoader mcl = moduleClassLoader != null ? moduleClassLoader : (ModuleClassLoader) getClass().getClassLoader();
    		
    		// force OpenMRS to expand this resource from the jar, if available.
    		// ideally we'd only look in this module, but this will also look in required modules...
    		mcl.findResource(resourcePrefix + path);
    		
    		File folderForModule = ModuleClassLoader.getLibCacheFolderForModule(mcl.getModule());
    		File resourceFile = new File(folderForModule, resourcePrefix + path);
    		return resourceFile.exists() ? resourceFile : null;
    	}
	}
}
