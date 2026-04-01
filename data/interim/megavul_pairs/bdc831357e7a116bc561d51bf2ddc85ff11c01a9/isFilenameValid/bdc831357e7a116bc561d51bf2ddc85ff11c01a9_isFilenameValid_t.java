class isFilenameValid {
static boolean isFilenameValid(Context context, File file) {
        final File[] whitelist;
        try {
            whitelist = new File[] {
                    context.getFilesDir().getCanonicalFile(),
                    context.getCacheDir().getCanonicalFile(),
                    Environment.getDownloadCacheDirectory().getCanonicalFile(),
                    Environment.getExternalStorageDirectory().getCanonicalFile(),
            };
        } catch (IOException e) {
            Log.w(TAG, "Failed to resolve canonical path: " + e);
            return false;
        }

        for (File testDir : whitelist) {
            if (FileUtils.contains(testDir, file)) {
                return true;
            }
        }

        return false;
    }
}
