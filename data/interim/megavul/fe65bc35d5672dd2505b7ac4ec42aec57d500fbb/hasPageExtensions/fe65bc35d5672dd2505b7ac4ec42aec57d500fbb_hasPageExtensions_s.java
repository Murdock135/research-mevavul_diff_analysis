class hasPageExtensions {
@Override
    public boolean hasPageExtensions(XWikiContext context)
    {
        XWikiDocument doc = context.getDoc();
        if (doc != null) {
            List<BaseObject> objects = doc.getObjects(getExtensionClassName());
            if (objects != null) {
                for (BaseObject obj : objects) {
                    if (obj == null) {
                        continue;
                    }
                    if (obj.getStringValue(USE_FIELDNAME).equals("currentPage")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
