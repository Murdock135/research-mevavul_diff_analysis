class addWorkUnits {
protected int addWorkUnits(List<WorkUnit> list) { // This appears to only be used by unit tests and the copy constructor
        if (workUnitList.size() + list.size() > MAX_UNITS) {
            throw new IllegalStateException("WorkBundle may not contain more than " + MAX_UNITS + " WorkUnits.");
        }
        workUnitList.addAll(list);
        return workUnitList.size();
    }
}
