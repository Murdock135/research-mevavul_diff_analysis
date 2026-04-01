class _isEnumValueOf {
protected boolean _isEnumValueOf(AnnotatedWithParams creator) {
        return ClassUtil.isEnumType(creator.getDeclaringClass())
                && "valueOf".equals(creator.getName());
    }
}
