class withCurrentDir {
public ImportedFiles withCurrentDir(AParentFolder newCurrentDir) {
		if (newCurrentDir == null) 
			return this;
		
		return new ImportedFiles(imported, newCurrentDir);
	}
}
