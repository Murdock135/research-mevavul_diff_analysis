class endOfCompressedDataReached {
private void endOfCompressedDataReached() throws IOException {
    //With inflater, without knowing the compressed or uncompressed size, we over read necessary data
    //In such cases, we have to push back the inputstream to the end of data
    int numberOfBytesPushedBack = decompressedInputStream.pushBackInputStreamIfNecessary(inputStream);

    //First signal the end of data for this entry so that ciphers can read any header data if applicable
    decompressedInputStream.endOfEntryReached(inputStream, numberOfBytesPushedBack);

    readExtendedLocalFileHeaderIfPresent();
    verifyCrc();
    resetFields();
    this.entryEOFReached = true;
  }
}
