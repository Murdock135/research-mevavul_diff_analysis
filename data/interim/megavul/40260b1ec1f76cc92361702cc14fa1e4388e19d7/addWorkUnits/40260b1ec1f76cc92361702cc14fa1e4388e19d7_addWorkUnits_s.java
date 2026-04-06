class addWorkUnits {
protected int addWorkUnits(List<WorkUnit> list) { // This appears to only be used by unit tests and the copy constructor
        workUnitList.addAll(list);
        return workUnitList.size();
    }
}
