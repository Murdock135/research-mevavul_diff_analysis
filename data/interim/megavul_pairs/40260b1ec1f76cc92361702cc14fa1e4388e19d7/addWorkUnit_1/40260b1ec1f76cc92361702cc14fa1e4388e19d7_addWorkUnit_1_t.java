class addWorkUnit_1 {
public int addWorkUnit(WorkUnit workUnit, long fileModificationTimeInMillis, long fileSize) {
        addWorkUnit(workUnit);

        if (fileModificationTimeInMillis < oldestFileModificationTime) {
            oldestFileModificationTime = fileModificationTimeInMillis;
        }
        if (fileModificationTimeInMillis > youngestFileModificationTime) {
            youngestFileModificationTime = fileModificationTimeInMillis;
        }
        totalFileSize += fileSize;
        return size();
    }
}
