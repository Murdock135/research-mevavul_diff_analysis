class untar {
protected void untar(File destDir, InputStream inputStream) throws IOException {
        TarArchiveInputStream tin = new TarArchiveInputStream(inputStream);
        TarArchiveEntry tarEntry = null;

        while ((tarEntry = tin.getNextTarEntry()) != null) {
            File destEntry = new File(destDir, tarEntry.getName());
            File parent = destEntry.getParentFile();

            if (!parent.exists()) {
                parent.mkdirs();
            }

            if (tarEntry.isDirectory()) {
                destEntry.mkdirs();
            } else {
                FileOutputStream fout = new FileOutputStream(destEntry);
                try {
                    IOUtils.copy(tin, fout);
                } finally {
                    fout.close();
                }
            }
        }

        tin.close();
    }
}
