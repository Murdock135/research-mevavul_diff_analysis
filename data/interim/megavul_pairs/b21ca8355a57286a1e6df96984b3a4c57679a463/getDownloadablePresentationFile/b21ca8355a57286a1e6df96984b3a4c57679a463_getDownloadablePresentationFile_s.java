class getDownloadablePresentationFile {
public File getDownloadablePresentationFile(String meetingId, String presId, String presFilename) {
    	log.info("Find downloadable presentation for meetingId={} presId={} filename={}", meetingId, presId, presFilename);

        File presDir = Util.getPresentationDir(presentationBaseDir, meetingId, presId);
        return new File(presDir.getAbsolutePath() + File.separatorChar + presFilename);
    }
}
