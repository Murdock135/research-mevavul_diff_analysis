class handleExtract {
private static boolean handleExtract(InputStream inputStream, File folder) {
		if (folder.exists() && !folder.isDirectory()) return false;
		if (!folder.exists() && !folder.mkdirs()) return false;
		
		ZipInputStream zipIn = null;
		
		try {
			zipIn = new ZipInputStream(inputStream);
			
			ZipEntry zipEntry;
			while ((zipEntry = zipIn.getNextEntry()) != null) {
				File file = new File(folder, zipEntry.getName());
				
				if (zipEntry.isDirectory()) {
					if (!file.exists() && !file.mkdirs()) return false;
				} else {
					if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) return false;
					if (!handleStreamCopy(zipIn, new FileOutputStream(file), false, true)) return false;
					zipIn.closeEntry();
				}
			}
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (zipIn != null) {
					zipIn.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
