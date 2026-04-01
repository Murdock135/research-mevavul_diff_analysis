class doPost {
@Override
    protected void doPost( HttpServletRequest request,
                           HttpServletResponse response ) throws ServletException, IOException {

        try {
            if ( request.getParameter( "path" ) != null ) {

                final URI uri = new URI( request.getParameter( "path" ) );

                if ( !validateAccess( uri, response ) ) {
                    return;
                }

                writeFile( ioService.get( uri ), getFileItem( request ) );

                writeResponse( response, "OK" );
            } else if ( request.getParameter( "folder" ) != null ) {

                final URI uri = new URI( request.getParameter( "folder" ) + "/" + request.getParameter( "fileName" ) );

                if ( !validateAccess( uri, response ) ) {
                    return;
                }

                writeFile(
                        ioService.get( uri ),
                        getFileItem( request ) );

                writeResponse( response, "OK" );
            }

        } catch ( FileUploadException e ) {
            logError( e );
            writeResponse( response, "FAIL" );
        } catch ( URISyntaxException e ) {
            logError( e );
            writeResponse( response, "FAIL" );
        }
    }
}
