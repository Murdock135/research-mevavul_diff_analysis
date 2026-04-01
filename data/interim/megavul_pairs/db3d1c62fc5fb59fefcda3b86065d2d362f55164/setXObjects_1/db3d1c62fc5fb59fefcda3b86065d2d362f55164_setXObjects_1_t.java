class setXObjects_1 {
public void setXObjects(DocumentReference classReference, List<BaseObject> objects)
    {
        // Remove existing objects
        List<BaseObject> existingbjects = this.xObjects.get(classReference);
        if (existingbjects != null) {
            existingbjects.clear();
        }

        for (BaseObject obj : objects) {
            obj.setOwnerDocument(this);
        }

        // Add new objects
        this.xObjects.put(classReference, new BaseObjects(objects));

        setMetaDataDirty(true);
    }
}
