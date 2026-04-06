class extractFile {
protected void extractFile(
            final File srcF,
            final File dir,
            final InputStream compressedInputStream,
            String entryName,
            final Date entryDate,
            final boolean isDirectory,
            final Integer mode,
            String symlinkDestination,
            final FileMapper[] fileMappers)
            throws IOException, ArchiverException {
        if (fileMappers != null) {
            for (final FileMapper fileMapper : fileMappers) {
                entryName = fileMapper.getMappedFileName(entryName);
            }
        }

        // Hmm. Symlinks re-evaluate back to the original file here. Unsure if this is a good thing...
        final File targetFileName = FileUtils.resolveFile(dir, entryName);

        // Make sure that the resolved path of the extracted file doesn't escape the destination directory
        // getCanonicalFile().toPath() is used instead of getCanonicalPath() (returns String),
        // because "/opt/directory".startsWith("/opt/dir") would return false negative.
        Path canonicalDirPath = dir.getCanonicalFile().toPath();
        Path canonicalDestPath = targetFileName.getCanonicalFile().toPath();

        if (!canonicalDestPath.startsWith(canonicalDirPath)) {
            throw new ArchiverException("Entry is outside of the target directory (" + entryName + ")");
        }

        try {
            if (!shouldExtractEntry(dir, targetFileName, entryName, entryDate)) {
                return;
            }

            // create intermediary directories - sometimes zip don't add them
            final File dirF = targetFileName.getParentFile();
            if (dirF != null) {
                dirF.mkdirs();
            }

            if (!StringUtils.isEmpty(symlinkDestination)) {
                SymlinkUtils.createSymbolicLink(targetFileName, new File(symlinkDestination));
            } else if (isDirectory) {
                targetFileName.mkdirs();
            } else {
                try (OutputStream out = Files.newOutputStream(targetFileName.toPath())) {
                    IOUtil.copy(compressedInputStream, out);
                }
            }

            targetFileName.setLastModified(entryDate.getTime());

            if (!isIgnorePermissions() && mode != null && !isDirectory) {
                ArchiveEntryUtils.chmod(targetFileName, mode);
            }
        } catch (final FileNotFoundException ex) {
            getLogger().warn("Unable to expand to file " + targetFileName.getPath());
        }
    }
}
