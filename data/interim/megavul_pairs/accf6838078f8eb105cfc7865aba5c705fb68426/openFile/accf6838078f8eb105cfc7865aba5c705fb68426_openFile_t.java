class openFile {
@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		try {
			String cacheDir = getContext().getCacheDir().toString();
			File privateFile = new File(cacheDir, uri.getLastPathSegment());

			if (!privateFile.getCanonicalPath().startsWith(cacheDir)) {
				throw new IllegalArgumentException();
			}

			return ParcelFileDescriptor.open(privateFile, ParcelFileDescriptor.MODE_READ_ONLY);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
