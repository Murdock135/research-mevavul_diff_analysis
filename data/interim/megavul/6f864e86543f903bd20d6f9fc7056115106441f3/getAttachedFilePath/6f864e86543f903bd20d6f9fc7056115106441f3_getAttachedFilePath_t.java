class getAttachedFilePath {
public static String getAttachedFilePath(String studyOid) throws Exception {    	
        String attachedFilePath = CoreResources.getField("attached_file_location");
        if (attachedFilePath == null || attachedFilePath.length() <= 0) {
            attachedFilePath = CoreResources.getField("filePath") + "attached_files" + File.separator;
        }
        File tempFile =  new File(attachedFilePath,studyOid);
        String canonicalPath= tempFile.getCanonicalPath();
        
        if (canonicalPath.startsWith(attachedFilePath)) {
        	if (attachedFilePath == null || attachedFilePath.length() <= 0) {
                attachedFilePath = CoreResources.getField("filePath") + "attached_files" + File.separator + studyOid + File.separator;
            } else {
                attachedFilePath += studyOid + File.separator;
            }
            return attachedFilePath;
        }else {
        	throw new RuntimeException("Traversal attempt - file path not allowed " + studyOid);
        }
        
    }
}
