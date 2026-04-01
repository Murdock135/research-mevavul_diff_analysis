class provide {
@Override
    public File provide() throws IOException
    {
        File f = File.createTempFile(_prefix, _suffix);
        f.deleteOnExit();
        return f;
    }
}
