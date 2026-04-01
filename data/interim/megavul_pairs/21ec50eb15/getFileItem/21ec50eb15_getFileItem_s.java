class getFileItem {
private FileItem getFileItem(HttpServletRequest request) throws FileUploadException {
        Iterator iterator = getServletFileUpload().parseRequest(request).iterator();
        while (iterator.hasNext()) {
            FileItem item = (FileItem) iterator.next();
            if (!item.isFormField()) {
                return item;
            }
        }
        return null;
    }
}
