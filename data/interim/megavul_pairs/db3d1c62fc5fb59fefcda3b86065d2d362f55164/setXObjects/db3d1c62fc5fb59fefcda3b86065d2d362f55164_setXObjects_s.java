class setXObjects {
public void setXObjects(Map<DocumentReference, List<BaseObject>> objects)
    {
        if (objects == null) {
            // Make sure we don`t set a null objects map since we assume everywhere that it is not null when using it.
            objects = new HashMap<>();
        }

        boolean isDirty = false;

        for (List<BaseObject> objList : objects.values()) {
            for (BaseObject obj : objList) {
                obj.setOwnerDocument(this);
                isDirty = true;
            }
        }

        // This operation resulted in marking the current document dirty.
        if (isDirty) {
            setMetaDataDirty(true);
        }

        // Replace the current objects with the provided ones.
        this.xObjects = objects;
    }
}
