class fileRange {
@GET
    @Path("io/file-range/{size}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response fileRange(@PathParam("size") long size) throws IOException {
        File file = Files.createTempFile("empty-file", "").toFile();
        file.deleteOnExit();
        RandomAccessFile rFile = new RandomAccessFile(file, "rw");
        rFile.setLength(size);
        rFile.close();
        return Response.ok(new FileRange(file, 0, size - 1)).build();
    }
}
