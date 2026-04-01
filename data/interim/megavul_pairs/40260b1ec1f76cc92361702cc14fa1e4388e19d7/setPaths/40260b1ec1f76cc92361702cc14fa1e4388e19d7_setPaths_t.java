class setPaths {
void setPaths(final String s) {
            try {
                final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(s.getBytes("8859_1")));
                this.paths = WorkBundle.readFromStream(dis);
            } catch (Exception e) {
                logger.error("Cannot deserialize WorkBundle using {} bytes", s.length(), e);
                throw new IllegalArgumentException("Cannot deserialize WorkBundle");
            }
        }
}
