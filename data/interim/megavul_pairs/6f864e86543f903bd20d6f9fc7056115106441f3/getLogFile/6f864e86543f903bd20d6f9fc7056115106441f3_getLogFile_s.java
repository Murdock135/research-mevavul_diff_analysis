class getLogFile {
@RequestMapping(value = "/forms/migrate/{filename}/downloadLogFile")
    public void getLogFile(@PathVariable("filename") String fileName, HttpServletResponse response) throws Exception {
        InputStream inputStream = null;
        try {
        	//Validate/Sanitize user input filename using a standard library, prevent from path traversal 
            String logFileName = getFilePath() + File.separator + FilenameUtils.getName(fileName);
            File fileToDownload = new File(logFileName);
            inputStream = new FileInputStream(fileToDownload);
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            logger.debug("Request could not be completed at this moment. Please try again.");
            logger.debug(e.getStackTrace().toString());
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.debug(e.getStackTrace().toString());
                    throw e;
                }
            }
        }

    }
}
