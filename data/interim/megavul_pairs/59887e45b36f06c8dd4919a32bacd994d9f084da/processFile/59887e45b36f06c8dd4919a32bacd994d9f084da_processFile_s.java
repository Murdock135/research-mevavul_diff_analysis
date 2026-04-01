class processFile {
public static String processFile(File file) throws IOException
	{
		System.out.println("Processing " + file.getCanonicalPath() + "...");

		Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
		byte[] inBytes = encodeURIComponent(
				readInputStream(new FileInputStream(file)),
				CHARSET_FOR_URL_ENCODING).getBytes("UTF-8");
		deflater.setInput(inBytes);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
				inBytes.length);
		deflater.finish();
		byte[] buffer = new byte[IO_BUFFER_SIZE];

		while (!deflater.finished())
		{
			int count = deflater.deflate(buffer); // returns the generated code... index  
			outputStream.write(buffer, 0, count);
		}

		outputStream.close();

		return encodeToString(outputStream.toByteArray(), false);
	}
}
