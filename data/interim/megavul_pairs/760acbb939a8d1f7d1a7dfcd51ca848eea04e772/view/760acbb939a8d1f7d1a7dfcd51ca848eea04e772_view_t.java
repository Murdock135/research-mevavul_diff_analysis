class view {
@RequestMapping("/view")
    public void view(@RequestParam String filename,
                     @RequestParam(required = false) String base,
                     @RequestParam(required = false) Integer tailLines,
                     HttpServletResponse response) throws IOException {

        Path path = loggingPath(base);
        securityCheck(path, filename);
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        FileProvider fileProvider = getFileProvider(path);
        if (tailLines != null) {
            fileProvider.tailContent(path, filename, response.getOutputStream(), tailLines);
        }
        else {
            fileProvider.streamContent(path, filename, response.getOutputStream());
        }
    }
}
