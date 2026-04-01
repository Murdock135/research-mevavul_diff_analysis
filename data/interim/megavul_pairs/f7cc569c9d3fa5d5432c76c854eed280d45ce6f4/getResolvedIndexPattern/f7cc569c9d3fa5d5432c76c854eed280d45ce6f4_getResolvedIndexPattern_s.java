class getResolvedIndexPattern {
public Set<String> getResolvedIndexPattern(final User user, final IndexNameExpressionResolver resolver, final ClusterService cs, final boolean appendUnresolved) {
            final String unresolved = getUnresolvedIndexPattern(user);
            final ImmutableSet.Builder<String> resolvedIndices = new ImmutableSet.Builder<>();

            final WildcardMatcher matcher = WildcardMatcher.from(unresolved);
            if (!(matcher instanceof WildcardMatcher.Exact)) {
                final String[] aliasesForPermittedPattern = cs.state().getMetadata().getIndicesLookup().entrySet().stream()
                        .filter(e -> e.getValue().getType() == ALIAS)
                        .filter(e -> matcher.test(e.getKey()))
                        .map(e -> e.getKey())
                        .toArray(String[]::new);
                if (aliasesForPermittedPattern.length > 0) {
                    final String[] resolvedAliases = resolver.concreteIndexNames(cs.state(), IndicesOptions.lenientExpandOpen(), aliasesForPermittedPattern);
                    resolvedIndices.addAll(Arrays.asList(resolvedAliases));
                }
            }

            if (Strings.isNotBlank(unresolved)) {
                final String[] resolvedIndicesFromPattern = resolver.concreteIndexNames(cs.state(), IndicesOptions.lenientExpandOpen(), unresolved);
                resolvedIndices.addAll(Arrays.asList(resolvedIndicesFromPattern));
            }

            if (appendUnresolved || resolvedIndices.build().isEmpty()) {
                resolvedIndices.add(unresolved);
            }
            return resolvedIndices.build();
        }
}
