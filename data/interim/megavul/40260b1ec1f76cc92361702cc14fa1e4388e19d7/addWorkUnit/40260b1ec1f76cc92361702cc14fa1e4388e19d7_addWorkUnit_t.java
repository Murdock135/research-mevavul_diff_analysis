class addWorkUnit {
public int addWorkUnit(WorkUnit workUnit) {
        if (workUnitList.size() >= MAX_UNITS) {
            throw new IllegalStateException("WorkBundle may not contain more than " + MAX_UNITS + " WorkUnits.");
        }
        workUnitList.add(workUnit);
        return size();
    }
}
