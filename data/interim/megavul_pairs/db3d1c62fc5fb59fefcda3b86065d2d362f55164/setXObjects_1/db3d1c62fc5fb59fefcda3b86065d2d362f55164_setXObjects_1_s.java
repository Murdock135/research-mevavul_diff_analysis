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
        if (objects.isEmpty()) {
            // Pretty wrong but can't remove that for retro compatibility reasons...
            // Note that it means that someone can put an unmodifiable list here make impossible to add any object of
            // this class.
            this.xObjects.put(classReference, objects);
        } else {
            for (BaseObject baseObject : objects) {
                addXObject(classReference, baseObject);
            }
        }

        setMetaDataDirty(true);
    }
}
