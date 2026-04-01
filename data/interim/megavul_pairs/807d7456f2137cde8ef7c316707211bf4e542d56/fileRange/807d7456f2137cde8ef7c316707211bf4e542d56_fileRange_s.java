class fileRange {
@GET
    @Path("io/file-range/{size}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response fileRange(@PathParam("size") long size) throws IOException {
        File file = File.createTempFile("empty-file", "");
        file.deleteOnExit();
        RandomAccessFile rFile = new RandomAccessFile(file, "rw");
        rFile.setLength(size);
        rFile.close();
        return Response.ok(new FileRange(file, 0, size - 1)).build();
    }
}
