class constructNonDefaultConstructor {
protected AnnotatedConstructor constructNonDefaultConstructor(ClassUtil.Ctor ctor,
            ClassUtil.Ctor mixin)
    {
        final int paramCount = ctor.getParamCount();
        if (_intr == null) { // when annotation processing is disabled
            return new AnnotatedConstructor(_typeContext, ctor.getConstructor(),
                    _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
        }

        /* Looks like JDK has discrepancy, whereas annotations for implicit 'this'
         * (for non-static inner classes) are NOT included, but type is?
         * Strange, sounds like a bug. Alas, we can't really fix that...
         */
        if (paramCount == 0) { // no-arg default constructors, can simplify slightly
            return new AnnotatedConstructor(_typeContext, ctor.getConstructor(),
                    collectAnnotations(ctor, mixin),
                    NO_ANNOTATION_MAPS);
        }
        // Also: enum value constructors
        AnnotationMap[] resolvedAnnotations;
        Annotation[][] paramAnns = ctor.getParameterAnnotations();
        if (paramCount != paramAnns.length) {
            // Limits of the work-around (to avoid hiding real errors):
            // first, only applicable for member classes and then either:

            resolvedAnnotations = null;
            Class<?> dc = ctor.getDeclaringClass();
            // (a) is enum, which have two extra hidden params (name, index)
            if (dc.isEnum() && (paramCount == paramAnns.length + 2)) {
                Annotation[][] old = paramAnns;
                paramAnns = new Annotation[old.length+2][];
                System.arraycopy(old, 0, paramAnns, 2, old.length);
                resolvedAnnotations = collectAnnotations(paramAnns, null);
            } else if (dc.isMemberClass()) {
                // (b) non-static inner classes, get implicit 'this' for parameter, not  annotation
                if (paramCount == (paramAnns.length + 1)) {
                    // hack attack: prepend a null entry to make things match
                    Annotation[][] old = paramAnns;
                    paramAnns = new Annotation[old.length+1][];
                    System.arraycopy(old, 0, paramAnns, 1, old.length);
                    paramAnns[0] = NO_ANNOTATIONS;
                    resolvedAnnotations = collectAnnotations(paramAnns, null);
                }
            }
            if (resolvedAnnotations == null) {
                throw new IllegalStateException(String.format(
"Internal error: constructor for %s has mismatch: %d parameters; %d sets of annotations",
ctor.getDeclaringClass().getName(), paramCount, paramAnns.length));
            }
        } else {
            resolvedAnnotations = collectAnnotations(paramAnns,
                    (mixin == null) ? null : mixin.getParameterAnnotations());
        }
        return new AnnotatedConstructor(_typeContext, ctor.getConstructor(),
                collectAnnotations(ctor, mixin), resolvedAnnotations);
    }
}
