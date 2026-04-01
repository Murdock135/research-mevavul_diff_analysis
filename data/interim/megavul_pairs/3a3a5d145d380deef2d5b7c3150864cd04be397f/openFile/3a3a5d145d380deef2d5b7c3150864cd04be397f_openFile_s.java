class openFile {
@Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        int match = sURLMatcher.match(uri);

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.d(TAG, "openFile: uri=" + uri + ", mode=" + mode + ", match=" + match);
        }

        if (match != MMS_PART_ID) {
            return null;
        }

        // Verify that the _data path points to mms data
        Cursor c = query(uri, new String[]{"_data"}, null, null, null);
        int count = (c != null) ? c.getCount() : 0;
        if (count != 1) {
            // If there is not exactly one result, throw an appropriate
            // exception.
            if (c != null) {
                c.close();
            }
            if (count == 0) {
                throw new FileNotFoundException("No entry for " + uri);
            }
            throw new FileNotFoundException("Multiple items at " + uri);
        }

        c.moveToFirst();
        int i = c.getColumnIndex("_data");
        String path = (i >= 0 ? c.getString(i) : null);
        c.close();

        if (path == null) {
            return null;
        }
        try {
            File filePath = new File(path);
            if (!filePath.getCanonicalPath()
                    .startsWith(getContext().getDir(PARTS_DIR_NAME, 0).getCanonicalPath())) {
                Log.e(TAG, "openFile: path "
                        + filePath.getCanonicalPath()
                        + " does not start with "
                        + getContext().getDir(PARTS_DIR_NAME, 0).getCanonicalPath());
                // Don't care return value
                filePath.delete();
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "openFile: create path failed " + e, e);
            return null;
        }

        return openFileHelper(uri, mode);
    }
}
