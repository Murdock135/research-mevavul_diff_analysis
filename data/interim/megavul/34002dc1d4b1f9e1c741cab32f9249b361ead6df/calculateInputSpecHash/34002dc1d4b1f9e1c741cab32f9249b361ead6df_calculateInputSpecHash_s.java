class calculateInputSpecHash {
private String calculateInputSpecHash(File inputSpecFile) throws IOException {

        URL inputSpecRemoteUrl = inputSpecRemoteUrl();

        File inputSpecTempFile = inputSpecFile;

        if (inputSpecRemoteUrl != null) {
            inputSpecTempFile = File.createTempFile("openapi-spec", ".tmp");

            URLConnection conn = inputSpecRemoteUrl.openConnection();
            if (isNotEmpty(auth)) {
                List<AuthorizationValue> authList = AuthParser.parse(auth);
                for (AuthorizationValue a : authList) {
                    conn.setRequestProperty(a.getKeyName(), a.getValue());
                }
            }
            try (ReadableByteChannel readableByteChannel = Channels.newChannel(conn.getInputStream())) {
                FileChannel fileChannel;
                try (FileOutputStream fileOutputStream = new FileOutputStream(inputSpecTempFile)) {
                    fileChannel = fileOutputStream.getChannel();
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                }
            }
        }

        ByteSource inputSpecByteSource =
                inputSpecTempFile.exists()
                        ? Files.asByteSource(inputSpecTempFile)
                        : CharSource.wrap(ClasspathHelper.loadFileFromClasspath(inputSpecTempFile.toString().replaceAll("\\\\","/")))
                        .asByteSource(StandardCharsets.UTF_8);

        return inputSpecByteSource.hash(Hashing.sha256()).toString();
    }
}
