class DoGetResumable {
protected void DoGetResumable(HttpServletRequest request, HttpServletResponse response) 
        throws IOException
    {
        if (ConfigurationManager.getProperty("upload.temp.dir") != null)
        {
            tempDir = ConfigurationManager.getProperty("upload.temp.dir");
        }
        else
        {
            tempDir = System.getProperty("java.io.tmpdir");
        }

        String resumableIdentifier = request.getParameter("resumableIdentifier");
        String resumableChunkNumber = request.getParameter("resumableChunkNumber");
        long resumableCurrentChunkSize = 
                Long.valueOf(request.getParameter("resumableCurrentChunkSize"));

        tempDir = tempDir + File.separator + resumableIdentifier;

        File fileDir = new File(tempDir);

        // create a new directory for each resumableIdentifier
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        // use the String "part" and the chunkNumber as filename of a chunk
        String chunkPath = tempDir + File.separator + "part" + resumableChunkNumber;

        File chunkFile = new File(chunkPath);
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
