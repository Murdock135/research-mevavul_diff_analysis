class addFileNames_1 {
protected int addFileNames(String[] file) { // This appears to only be used by unit tests
        for (int i = 0; file != null && i < file.length; i++) {
            workUnitList.add(new WorkUnit(file[i]));
        }
        return size();
    }
}
