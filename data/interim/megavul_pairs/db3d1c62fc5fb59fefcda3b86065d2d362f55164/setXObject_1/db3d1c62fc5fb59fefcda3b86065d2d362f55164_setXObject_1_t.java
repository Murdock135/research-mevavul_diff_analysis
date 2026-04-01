class setXObject_1 {
public void setXObject(int nb, BaseObject object)
    {
        object.setOwnerDocument(this);
        object.setNumber(nb);

        BaseObjects objects = this.xObjects.get(object.getXClassReference());
        if (objects == null) {
            objects = new BaseObjects();
            this.xObjects.put(object.getXClassReference(), objects);
        }
        while (nb >= objects.size()) {
            objects.add(null);
        }
        objects.set(nb, object);
        setMetaDataDirty(true);
    }
}
