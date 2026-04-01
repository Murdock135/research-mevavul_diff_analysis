class addFileNames_1 {
protected int addFileNames(String[] file) { // This appears to only be used by unit tests
        for (String f : file) {
            addWorkUnit(new WorkUnit(f));
        }
        return size();
    }
}
