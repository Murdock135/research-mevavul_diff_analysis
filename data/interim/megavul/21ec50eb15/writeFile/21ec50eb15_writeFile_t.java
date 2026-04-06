class writeFile {
private void writeFile( Path path,
                            FileItem uploadedItem ) throws IOException {
        if ( !ioService.exists( path ) ) {
            ioService.createFile( path );
        }

        ioService.write( path, IOUtils.toByteArray( uploadedItem.getInputStream() ) );

        uploadedItem.getInputStream().close();
    }
}
