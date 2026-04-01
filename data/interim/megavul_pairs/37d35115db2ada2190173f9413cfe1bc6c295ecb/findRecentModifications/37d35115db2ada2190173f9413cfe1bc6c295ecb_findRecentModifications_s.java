class findRecentModifications {
List<Modification> findRecentModifications(int count) {
        // Currently impossible to check modifications on a remote repository.
        InMemoryStreamConsumer consumer = inMemoryConsumer();
        bombUnless(pull(consumer), "Failed to run hg pull command: " + consumer.getAllOutput());
        CommandLine hg = hg("log", "--limit", String.valueOf(count), "-b", branch, "--style", templatePath());
        return new HgModificationSplitter(execute(hg)).modifications();
    }
}
