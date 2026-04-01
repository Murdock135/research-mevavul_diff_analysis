class setObject {
@Override
    public T setObject(K name, Iterable<?> values) {
        validateName(nameValidator, false, name);

        int h = hashingStrategy.hashCode(name);
        int i = index(h);

        remove0(h, i, name);
        for (Object v: values) {
            if (v == null) {
                break;
            }
            add0(h, i, name, fromObject(name, v));
        }

        return thisT();
    }
}
