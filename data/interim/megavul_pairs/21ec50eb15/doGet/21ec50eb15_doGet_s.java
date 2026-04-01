class doGet {
@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            Path path = ioService.get(new URI(request.getParameter("path")));

            byte[] bytes = ioService.readAllBytes(path);

            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=%s;", path.getFileName().toString()));

            response.setContentType("application/octet-stream");

            response.getOutputStream().write(
                    bytes,
                    0,
                    bytes.length);

        } catch (URISyntaxException e) {
            logger.error("Failed to download a file.", e);
        }

    }
}
