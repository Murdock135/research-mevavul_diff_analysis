class createXObject {
public int createXObject(EntityReference classReference, XWikiContext context) throws XWikiException
    {
        DocumentReference absoluteClassReference = resolveClassReference(classReference);
        BaseObject object = BaseClass.newCustomClassInstance(absoluteClassReference, context);
        object.setOwnerDocument(this);
        object.setXClassReference(classReference);
        List<BaseObject> objects = this.xObjects.get(absoluteClassReference);
        if (objects == null) {
            objects = new ArrayList<BaseObject>();
            this.xObjects.put(absoluteClassReference, objects);
        }
        objects.add(object);
        int nb = objects.size() - 1;
        object.setNumber(nb);
        setMetaDataDirty(true);
        return nb;
    }
}
