class addFileNames {
protected int addFileNames(List<String> list) { // This appears to only be used by unit tests and the copy
                                                    // constructor
        for (String file : list) {
            workUnitList.add(new WorkUnit(file));
        }
        return size();
    }
}
