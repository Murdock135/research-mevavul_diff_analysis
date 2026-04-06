class DoGetResumable {
protected void DoGetResumable(Context context, HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        String baseDir;

        if (ConfigurationManager.getProperty("upload.temp.dir") != null)
        {
            baseDir = ConfigurationManager.getProperty("upload.temp.dir");
        }
        else
        {
            baseDir = System.getProperty("java.io.tmpdir");
        }

        String resumableIdentifier = request.getParameter("resumableIdentifier");
        String resumableChunkNumber = request.getParameter("resumableChunkNumber");
        long resumableCurrentChunkSize = 
                Long.valueOf(request.getParameter("resumableCurrentChunkSize"));

        tempDir = baseDir + File.separator + resumableIdentifier;

        File fileDir = new File(tempDir);

        // Test fileDir to see if canonical path is within the original baseDir
        if(!fileDir.getCanonicalPath().startsWith(baseDir)) {
            log.error("Error processing resumable upload chunk: temporary chunk file would be created outside " +
                    "permissible temp dir ("+ baseDir +") for submitter: " + context.getCurrentUser().getEmail());
            throw new IOException("Error processing resumableIdentifier: " + resumableIdentifier +
                    " (submitter: " + context.getCurrentUser().getEmail() + ")" +
                    ". Temporary upload directory would be created outside permissible base temp dir ("+ baseDir +")");
        }

        // create a new directory for each resumableIdentifier
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        // use the String "part" and the chunkNumber as filename of a chunk
        String chunkPath = tempDir + File.separator + "part" + resumableChunkNumber;

        File chunkFile = new File(chunkPath);

        // Test chunkFile to see if canonical path is within the original baseDir
        if(!chunkFile.getCanonicalPath().startsWith(baseDir)) {
            log.error("Error processing resumable upload chunk: temporary chunk file would be created outside " +
                    "permissible temp dir ("+ baseDir +") for submitter: " + context.getCurrentUser().getEmail());
            throw new IOException("Error processing resumableIdentifier: " + resumableIdentifier +
                    " (submitter: " + context.getCurrentUser().getEmail() + ")" +
                    ". Temporary upload directory would be created outside permissible base temp dir ("+ baseDir +")");
        }

        // if the chunk was uploaded already, we send a status code of 200
        if (chunkFile.exists()) {
            if (chunkFile.length() == resumableCurrentChunkSize) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            // The chunk file does not have the expected size, delete it and 
            // pretend that it wasn't uploaded already.
            chunkFile.delete();
        }
        // if we don't have the chunk send a http status code 404
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
