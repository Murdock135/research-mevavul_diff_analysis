class add {
@TestOnly
    public void add(File fileToAdd) {
        String[] args = new String[]{"add", "--", fileToAdd.getName()};
        CommandLine gitAdd = gitWd().withArgs(args);
        runOrBomb(gitAdd);
    }
}
