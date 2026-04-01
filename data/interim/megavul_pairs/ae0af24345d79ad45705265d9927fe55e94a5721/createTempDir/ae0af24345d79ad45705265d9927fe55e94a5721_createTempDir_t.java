class createTempDir {
public static final File createTempDir() throws IOException
   {
      File dir = Files.createTempFile("mpxj", "tmp").toFile();
      delete(dir);
      mkdirs(dir);
      return dir;
   }
}
