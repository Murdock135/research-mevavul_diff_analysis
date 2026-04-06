class writeStreamToTempFile {
public static File writeStreamToTempFile(InputStream inputStream, String tempFileSuffix) throws IOException
   {
      FileOutputStream outputStream = null;

      try
      {
         File file = File.createTempFile("mpxj", tempFileSuffix);
         outputStream = new FileOutputStream(file);
         byte[] buffer = new byte[1024];
         while (true)
         {
            int bytesRead = inputStream.read(buffer);
            if (bytesRead == -1)
            {
               break;
            }
            outputStream.write(buffer, 0, bytesRead);
         }
         return file;
      }

      finally
      {
         if (outputStream != null)
         {
            outputStream.close();
         }
      }
   }
}
