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
            V converted = fromObject(name, v);
            validateValue(valueValidator, name, converted);
            add0(h, i, name, converted);
        }

        return thisT();
    }
}
