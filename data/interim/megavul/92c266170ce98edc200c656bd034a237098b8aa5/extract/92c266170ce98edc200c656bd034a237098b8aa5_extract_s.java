class extract {
private boolean extract(ArrayList<String> errors, URL source, File target) {
        FileOutputStream os = null;
        InputStream is = null;
        boolean extracting = false;
        try {
            if (!target.exists() || isStale(source, target) ) {
                is = source.openStream();
                if (is != null) {
                    byte[] buffer = new byte[4096];
                    os = new FileOutputStream(target);
                    extracting = true;
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        os.write(buffer, 0, read);
                    }
                    os.close();
                    is.close();
                    chmod("755", target);
                }
            }
        } catch (Throwable e) {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e1) {
            }
            try {
                if (is != null)
                    is.close();
            } catch (IOException e1) {
            }
            if (extracting && target.exists())
                target.delete();
            errors.add(e.getMessage());
            return false;
        }
        return true;
    }
}
