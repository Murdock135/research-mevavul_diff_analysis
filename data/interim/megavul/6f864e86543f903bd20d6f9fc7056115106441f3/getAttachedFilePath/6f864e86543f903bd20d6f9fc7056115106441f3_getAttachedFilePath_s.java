class getAttachedFilePath {
public static String getAttachedFilePath(String inputStudyOid) {
    	// Using a standard library to validate/Sanitize user inputs which will be used in path expression to prevent from path traversal
    	String studyOid =  FilenameUtils.getName(inputStudyOid);
        String attachedFilePath = CoreResources.getField("attached_file_location");
        if (attachedFilePath == null || attachedFilePath.length() <= 0) {
            attachedFilePath = CoreResources.getField("filePath") + "attached_files" + File.separator + studyOid + File.separator;
        } else {
            attachedFilePath += studyOid + File.separator;
        }
        return attachedFilePath;
    }
}
