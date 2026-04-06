class getServletFileUpload {
private ServletFileUpload getServletFileUpload() {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        upload.setHeaderEncoding( "UTF-8" );
        return upload;
    }
}
