class copyDir {
private void copyDir(String source, File target) throws Exception {
    //Let's wrap the code to a runnable inner class to avoid NoClassDef on Option classes.
    try {
    new Runnable() {
      public void  run() {
        File destination = target;
        if (!destination.isDirectory() && !destination.mkdirs())

        {
          throw KubernetesClientException.launderThrowable(new IOException("Failed to create directory: " + destination));
        }
        try (
          InputStream is = readTar(source);
          org.apache.commons.compress.archivers.tar.TarArchiveInputStream tis = new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(is))

        {
          for (org.apache.commons.compress.archivers.ArchiveEntry entry = tis.getNextTarEntry(); entry != null; entry = tis.getNextEntry()) {
            if (tis.canReadEntryData(entry)) {
              File f = new File(destination, entry.getName());
              if (entry.isDirectory()) {
                if (!f.isDirectory() && !f.mkdirs()) {
                  throw new IOException("Failed to create directory: " + f);
                }
              } else {
                File parent = f.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                  throw new IOException("Failed to create directory: " + f);
                }
                try (OutputStream fs = new FileOutputStream(f)) {
                  System.out.println("Writing: " + f.getCanonicalPath());
                  BlockingInputStreamPumper pumper = new BlockingInputStreamPumper(tis, new Callback<byte[]>() {
                    @Override
                    public void call(byte[] input) {
                      try {
                        fs.write(input);
                      } catch (IOException e) {
                        throw KubernetesClientException.launderThrowable(e);
                      }
                    }
                  }, () -> {
                    try {
                      fs.close();
                    } catch (IOException e) {
                      throw KubernetesClientException.launderThrowable(e);
                    }
                  });
                  pumper.run();
                }
              }
            }
          }
        } catch (Exception e) {
          throw KubernetesClientException.launderThrowable(e);
        }
      }
    }.run();
     } catch (NoClassDefFoundError e) {
      throw new KubernetesClientException("TarArchiveInputStream class is provided by commons-codec, an optional dependency. To use the read/copy functionality you must explicitly add this dependency to the classpath.");
    }
  }
}
