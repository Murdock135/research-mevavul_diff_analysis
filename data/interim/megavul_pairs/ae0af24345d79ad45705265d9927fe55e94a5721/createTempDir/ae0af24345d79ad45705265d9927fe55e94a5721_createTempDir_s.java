class createTempDir {
public static final File createTempDir() throws IOException
   {
      File dir = File.createTempFile("mpxj", "tmp");
      delete(dir);
      mkdirs(dir);
      return dir;
   }
}
