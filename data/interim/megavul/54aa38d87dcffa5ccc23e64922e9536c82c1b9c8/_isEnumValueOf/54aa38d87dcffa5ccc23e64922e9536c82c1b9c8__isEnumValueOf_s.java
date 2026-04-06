class _isEnumValueOf {
protected boolean _isEnumValueOf(AnnotatedWithParams creator) {
        return creator.getDeclaringClass().isEnum()
                && "valueOf".equals(creator.getName());
    }
}
