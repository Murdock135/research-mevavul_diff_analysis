class loadStringData {
private String loadStringData(String path, String charset) throws EaterException, UnsupportedEncodingException {

		byte[] byteData = null;
		if (path.startsWith("http://") || path.startsWith("https://")) {
			final SURL url = SURL.create(path);
			if (url == null)
				throw EaterException.located("load JSON: Invalid URL " + path);
			byteData = url.getBytes();
			// ::comment when __CORE__
		} else {
			try {
				final SFile file = FileSystem.getInstance().getFile(path);
				if (file != null && file.exists() && file.canRead() && !file.isDirectory()) {
					final ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 8);
					FileUtils.copyToStream(file, out);
					byteData = out.toByteArray();
				}
			} catch (IOException e) {
				Logme.error(e);
				throw EaterException.located("load JSON: Cannot read file " + path + ". " + e.getMessage());
			}
			// ::done
		}

		if (byteData == null || byteData.length == 0)
			return null; // no length, no data (we want the default)

		return new String(byteData, charset);

	}
}
