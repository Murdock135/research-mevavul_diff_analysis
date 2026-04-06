class provide {
@Override
    public File provide() throws IOException
    {
        File f = Files.createTempFile(_prefix, _suffix).toFile();
        f.deleteOnExit();
        return f;
    }
}
