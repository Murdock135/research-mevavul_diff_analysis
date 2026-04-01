class view {
@RequestMapping("/view")
    public void view(@RequestParam String filename,
                     @RequestParam(required = false) String base,
                     @RequestParam(required = false) Integer tailLines,
                     HttpServletResponse response) throws IOException {
        securityCheck(filename);
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        Path path = loggingPath(base);
        FileProvider fileProvider = getFileProvider(path);
        if (tailLines != null) {
            fileProvider.tailContent(path, filename, response.getOutputStream(), tailLines);
        }
        else {
            fileProvider.streamContent(path, filename, response.getOutputStream());
        }
    }
}
