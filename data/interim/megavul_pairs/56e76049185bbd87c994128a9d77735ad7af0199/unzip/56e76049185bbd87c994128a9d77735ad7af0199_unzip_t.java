class unzip {
public static String unzip(File zipfile, String destDir) throws IOException {
        // 2
        // does the zip file exist and can we write to the temp directory
        if (!zipfile.canRead())
        {
            log.error("Zip file '" + zipfile.getAbsolutePath() + "' does not exist, or is not readable.");
        }

        String destinationDir = destDir;
        if (destinationDir == null){
        	destinationDir = tempWorkDir;
        }
        log.debug("Using directory " + destinationDir + " for zip extraction. (destDir arg is " + destDir +
                ", tempWorkDir is " + tempWorkDir + ")");

        File tempdir = new File(destinationDir);
        if (!tempdir.isDirectory())
        {
            log.error("'" + ConfigurationManager.getProperty("org.dspace.app.batchitemimport.work.dir") +
                    "' as defined by the key 'org.dspace.app.batchitemimport.work.dir' in dspace.cfg " +
                    "is not a valid directory");
        }

        if (!tempdir.exists() && !tempdir.mkdirs())
        {
            log.error("Unable to create temporary directory: " + tempdir.getAbsolutePath());
        }

        if(!destinationDir.endsWith(System.getProperty("file.separator"))) {
            destinationDir += System.getProperty("file.separator");
        }

        String sourcedir = destinationDir + zipfile.getName();
        String zipDir = destinationDir + zipfile.getName() + System.getProperty("file.separator");

        log.debug("zip directory to use is " + zipDir);

        // 3
        String sourceDirForZip = sourcedir;
        ZipFile zf = new ZipFile(zipfile);
        ZipEntry entry;
        Enumeration<? extends ZipEntry> entries = zf.entries();
        while (entries.hasMoreElements())
        {
            entry = entries.nextElement();
            // Check that the true path to extract files is never outside allowed temp directories
            // without creating any actual files on disk
            log.debug("Inspecting entry name: " + entry.getName() + " for path traversal security");
            File potentialExtract = new File(zipDir + entry.getName());
            String canonicalPath = potentialExtract.getCanonicalPath();
            log.debug("Canonical path to potential File is " + canonicalPath);
            if(!canonicalPath.startsWith(zipDir)) {
                log.error("Rejecting zip file: " + zipfile.getName() + " as it contains an entry that would be extracted " +
                        "outside the temporary unzip directory: " + canonicalPath);
                throw new IOException("Error extracting " + zipfile + ": Canonical path of zip entry: " +
                        entry.getName() + " (" + canonicalPath + ") does not start with permissible temp " +
                        "unzip directory (" + destinationDir + ")");
            }
            if (entry.isDirectory())
            {
                // Log error and throw IOException if a directory entry could not be created
                File newDir = new File(zipDir + entry.getName());
                if (!newDir.mkdirs()) {
                    log.error("Unable to create contents directory: " + zipDir + entry.getName());
                    throw new IOException("Unable to create contents directory: " + zipDir + entry.getName());
                }
            }
            else
            {
                System.out.println("Extracting file: " + entry.getName());
                log.info("Extracting file: " + entry.getName());

                int index = entry.getName().lastIndexOf('/');
                if (index == -1)
                {
                    // Was it created on Windows instead?
                    index = entry.getName().lastIndexOf('\\');
                }
                if (index > 0)
                {
                    File dir = new File(zipDir + entry.getName().substring(0, index));
                    if (!dir.exists() && !dir.mkdirs())
                    {
                        log.error("Unable to create directory: " + dir.getAbsolutePath());
                    }

                    //Entries could have too many directories, and we need to adjust the sourcedir
                    // file1.zip (SimpleArchiveFormat / item1 / contents|dublin_core|...
                    //            SimpleArchiveFormat / item2 / contents|dublin_core|...
                    // or
                    // file2.zip (item1 / contents|dublin_core|...
                    //            item2 / contents|dublin_core|...

                    //regex supports either windows or *nix file paths
                    String[] entryChunks = entry.getName().split("/|\\\\");
                    if(entryChunks.length > 2) {
                        if(sourceDirForZip == sourcedir) {
                            sourceDirForZip = sourcedir + "/" + entryChunks[0];
                        }
                    }


                }
                byte[] buffer = new byte[1024];
                int len;
                InputStream in = zf.getInputStream(entry);
                log.debug("Reading " + zipDir + entry.getName() + " into InputStream");
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(zipDir + entry.getName()));
                while((len = in.read(buffer)) >= 0)
                {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            }
        }

        //Close zip file
        zf.close();
        
        if(sourceDirForZip != sourcedir) {
            sourcedir = sourceDirForZip;
            System.out.println("Set sourceDir using path inside of Zip: " + sourcedir);
            log.info("Set sourceDir using path inside of Zip: " + sourcedir);
        }

        return sourcedir;
    }
}
