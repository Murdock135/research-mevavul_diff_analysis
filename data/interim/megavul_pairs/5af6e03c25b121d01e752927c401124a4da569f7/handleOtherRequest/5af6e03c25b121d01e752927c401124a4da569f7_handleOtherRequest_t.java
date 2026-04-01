class handleOtherRequest {
private void handleOtherRequest(String pathInfo, HttpServletResponse response) throws IOException {
        String[] parts = pathInfo.split("/");
        // Image request must be in correct format.
        if (parts.length < 3) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contextPath = "";
        int index = pathInfo.indexOf(parts[1]);
        if (index != -1) {
            contextPath = pathInfo.substring(index + parts[1].length());
        }

        File pluginDirectory = new File(JiveGlobals.getHomeDirectory(), "plugins");
        File file = new File(pluginDirectory, parts[1] + File.separator + "web" + contextPath);

        // When using dev environment, the images dir may be under something other that web.
        Plugin plugin = pluginManager.getPlugin(parts[1]);
        PluginDevEnvironment environment = pluginManager.getDevEnvironment(plugin);

        if (environment != null) {
            file = new File(environment.getWebRoot(), contextPath);
        } else {
            if ( !ALLOW_LOCAL_FILE_READING.getValue() ) {
                // If _not_ in a DEV environment, ensure that the file that's being served is a
                // file that is part of Openfire. This guards against accessing files from the
                // operating system, or other files that shouldn't be accessible via the web (OF-1886).
                final Path absoluteHome = new File( JiveGlobals.getHomeDirectory() ).toPath().normalize().toAbsolutePath();
                final Path absoluteLookup = file.toPath().normalize().toAbsolutePath();
                if ( !absoluteLookup.startsWith( absoluteHome ) )
                {
                    response.setStatus( HttpServletResponse.SC_FORBIDDEN );
                    return;
                }
            }
        }

        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(pathInfo);
        if (contentType == null) {
            contentType = "text/plain";
        }
        response.setContentType(contentType);
        // Write out the resource to the user.
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            try (ServletOutputStream out = response.getOutputStream()) {

                // Set the size of the file.
                response.setContentLength((int) file.length());

                // Use a 1K buffer.
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}
