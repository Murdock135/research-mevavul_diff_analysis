class parseModuleConfigFile {
protected void parseModuleConfigFile(Digester digester, String path)
        throws UnavailableException {

        InputStream input = null;
        try {
            URL url = getServletContext().getResource(path);

            // If the config isn't in the servlet context, try the class loader
            // which allows the config files to be stored in a jar
            if (url == null) {
                url = getClass().getResource(path);
            }

            if (url == null) {
                String msg = internal.getMessage("configMissing", path);
                log.error(msg);
                throw new UnavailableException(msg);
            }

            InputSource is = new InputSource(url.toExternalForm());
            input = url.openStream();
            is.setByteStream(input);
            digester.parse(is);

        } catch (MalformedURLException e) {
            handleConfigException(path, e);
        } catch (IOException e) {
            handleConfigException(path, e);
        } catch (SAXException e) {
            handleConfigException(path, e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new UnavailableException(e.getMessage());
                }
            }
        }
    }
}
