class setXObject {
@Deprecated
    public void setXObject(DocumentReference classReference, int nb, BaseObject object)
    {
        if (object != null) {
            object.setOwnerDocument(this);
            object.setNumber(nb);
        }

        List<BaseObject> objects = this.xObjects.get(classReference);
        if (objects == null) {
            objects = new ArrayList<BaseObject>();
            this.xObjects.put(classReference, objects);
        }
        while (nb >= objects.size()) {
            objects.add(null);
        }
        objects.set(nb, object);
        setMetaDataDirty(true);
    }
}
