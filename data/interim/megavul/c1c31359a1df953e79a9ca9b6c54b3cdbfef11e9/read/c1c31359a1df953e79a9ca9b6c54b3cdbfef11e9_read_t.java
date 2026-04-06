class read {
private Externalizable read(InputStream in, String className, ClassLoader classLoader) throws Exception {
            Externalizable ds = ClassLoaderUtil.newInstance(classLoader, className);
            ObjectInputStream objectInputStream = newObjectInputStream(classLoader, classFilter, in);
            ds.readExternal(objectInputStream);
            return ds;
        }
}
