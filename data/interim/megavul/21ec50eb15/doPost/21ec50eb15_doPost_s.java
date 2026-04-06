class doPost {
@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            if (request.getParameter("path") != null) {
                writeFile(ioService.get(new URI(request.getParameter("path"))), getFileItem(request));

                writeResponse(response, "OK");
            } else if (request.getParameter("folder") != null) {
                writeFile(
                        ioService.get(new URI(request.getParameter("folder") + "/" + request.getParameter("fileName"))),
                        getFileItem(request));

                writeResponse(response, "OK");
            }

        } catch (FileUploadException e) {
            logError(e);
            writeResponse(response, "FAIL");
        } catch (URISyntaxException e) {
            logError(e);
            writeResponse(response, "FAIL");
        }
    }
}
