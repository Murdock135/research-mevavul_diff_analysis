class getDownloadablePresentationFile {
public File getDownloadablePresentationFile(String meetingId, String presId, String presFilename) {
        log.info("Find downloadable presentation for meetingId={} presId={} filename={}", meetingId, presId,
                presFilename);
        File presDir = Util.getPresentationDir(presentationBaseDir, meetingId, presId);
        // Build file to presFilename
        // Get canonicalPath and make sure it starts with
        // /var/bigbluebutton/<meetingid-pattern>
        // If so return file, if not return null
        File presFile = new File(presDir.getAbsolutePath() + File.separatorChar + presFilename);
        try {
            String presFileCanonical = presFile.getCanonicalPath();
            log.debug("Requested presentation name file full path {}",presFileCanonical);
            if (presFileCanonical.startsWith(presentationBaseDir)) {
                return presFile;
            }
        } catch (IOException e) {
            log.error("Exception getting canonical path for {}.\n{}", presFilename, e);
            return null;
        }

        log.error("Cannot find file for {}.", presFilename);

        return null;
    }
}
