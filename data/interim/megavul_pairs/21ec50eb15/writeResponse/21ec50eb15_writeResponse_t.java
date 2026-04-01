class writeResponse {
private void writeResponse( HttpServletResponse response,
                                String ok ) throws IOException {
        response.setContentType( "text/html" );
        response.getWriter().write( ok );
    }
}
