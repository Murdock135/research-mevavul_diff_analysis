class extractZip {
public static void extractZip(File zipFile, File destDir) throws IOException
    {
        byte[] buffer = new byte[1024];
        if (!destDir.exists())
            destDir.mkdirs();

        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry ze = zis.getNextEntry();
        try
        {
            while (ze != null)
            {
                String fileName = ze.getName();
                File newFile = new File(destDir, fileName);
                if (ze.isDirectory())
                {
                    if (newFile.exists())
                        deleteDirAndContents(newFile);
                    newFile.mkdirs();
                }
                else
                {
                    if (newFile.exists())
                        newFile.delete();
                    if (newFile.getParentFile() != null && !newFile.getParentFile().exists())
                        newFile.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0)
                        fos.write(buffer, 0, len);

                    fos.close();
                }
                ze = zis.getNextEntry();
            }
        }
        finally
        {
            zis.closeEntry();
            zis.close();
        }
    }
}
