class processFile {
public static String processFile(File file) throws IOException
	{
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
		byte[] inBytes = readInputStream(new FileInputStream(file)).getBytes("UTF-8");
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

		return Base64.getEncoder().encodeToString(outputStream.toByteArray());
	}
}
