class setXObject {
@Deprecated
    public void setXObject(DocumentReference classReference, int nb, BaseObject object)
    {
        if (object != null) {
            object.setOwnerDocument(this);
            object.setNumber(nb);
        }

        BaseObjects objects = this.xObjects.get(classReference);
        if (objects == null) {
            objects = new BaseObjects();
            this.xObjects.put(classReference, objects);
        }
        while (nb >= objects.size()) {
            objects.add(null);
        }
        objects.set(nb, object);
        setMetaDataDirty(true);
    }
}
