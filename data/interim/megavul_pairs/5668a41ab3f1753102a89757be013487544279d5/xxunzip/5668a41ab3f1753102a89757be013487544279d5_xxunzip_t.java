class xxunzip {
private static void xxunzip(InputStream is, String outdir) throws IOException {
		final Path outPath = Paths.get(outdir);
		
		try(ZipInputStream zis = new ZipInputStream (new BufferedInputStream(is))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				Path filePath = Paths.get(outdir, entry.getName());
				Path normalizedPath = filePath.normalize();
				if(!normalizedPath.startsWith(outPath)) {
					throw new IOException("Invalid ZIP");
				}
				
				File of = new File(outdir, entry.getName());
				if (entry.isDirectory()) {
					of.mkdirs();
				} else {
					File parent = of.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					xxunzipcpio(zis, of);
				}
			}
		} catch (IllegalArgumentException e) {
			//problem with chars in entry name likely
		}
	}
}
