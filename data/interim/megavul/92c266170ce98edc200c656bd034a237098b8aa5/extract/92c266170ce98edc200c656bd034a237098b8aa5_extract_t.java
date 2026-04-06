class extract {
private File extract(ArrayList<String> errors, URL source, String prefix, String suffix, File directory) {
        File target = null;
        try {
            FileOutputStream os = null;
            InputStream is = null;
            try {
                target = File.createTempFile(prefix, suffix, directory);
                is = source.openStream();
                if (is != null) {
                    byte[] buffer = new byte[4096];
                    os = new FileOutputStream(target);
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        os.write(buffer, 0, read);
                    }
                    chmod("755", target);
                }
                target.deleteOnExit();
                return target;
            } finally {
                close(os);
                close(is);
            }
        } catch (Throwable e) {
            if( target!=null ) {
                target.delete();
            }
            errors.add(e.getMessage());
        }
        return null;
    }
}
