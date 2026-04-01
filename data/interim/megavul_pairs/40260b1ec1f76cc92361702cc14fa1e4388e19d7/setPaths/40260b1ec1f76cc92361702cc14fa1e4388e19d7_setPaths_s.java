class setPaths {
void setPaths(final String s) {
            try {
                final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(s.getBytes("8859_1")));
                this.paths = (WorkBundle) ois.readObject();
            } catch (Exception e) {
                logger.error("Cannot deserialize WorkBundle using {} bytes", s.length(), e);
                throw new IllegalArgumentException("Cannot deserialize WorkBundle");
            }
        }
}
