class doGet {
@Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response )
            throws ServletException, IOException {

        try {

            final URI uri = new URI( request.getParameter( "path" ) );

            if ( !validateAccess( uri, response ) ) {
                return;
            }

            final Path path = ioService.get( uri );

            byte[] bytes = ioService.readAllBytes( path );

            response.setHeader( "Content-Disposition",
                                format( "attachment; filename=%s;", path.getFileName().toString() ) );

            response.setContentType( "application/octet-stream" );

            response.getOutputStream().write(
                    bytes,
                    0,
                    bytes.length );

        } catch ( final Exception e ) {
            logger.error( "Failed to download a file.", e );
        }

    }
}
