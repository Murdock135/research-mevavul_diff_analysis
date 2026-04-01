class untar {
protected void untar(File destDir, InputStream inputStream) throws IOException {
        TarArchiveInputStream tin = new TarArchiveInputStream(inputStream);
        TarArchiveEntry tarEntry = null;

        while ((tarEntry = tin.getNextTarEntry()) != null) {
            File destEntry = new File(destDir, tarEntry.getName());
            if (!destEntry.toPath().normalize().startsWith(destDir.toPath().normalize())) {
                throw new IllegalArgumentException("Zip archives with files escaping their root directory are not allowed.");
            }
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
