class getResolvedIndexPattern {
public Set<String> getResolvedIndexPattern(final User user, final IndexNameExpressionResolver resolver, final ClusterService cs, final boolean appendUnresolved) {
            final String unresolved = getUnresolvedIndexPattern(user);
            final ImmutableSet.Builder<String> resolvedIndices = new ImmutableSet.Builder<>();

            final WildcardMatcher matcher = WildcardMatcher.from(unresolved);
            boolean includeDataStreams = true;
            if (!(matcher instanceof WildcardMatcher.Exact)) {
                final String[] aliasesAndDataStreamsForPermittedPattern = cs.state().getMetadata().getIndicesLookup().entrySet().stream()
                        .filter(e -> (e.getValue().getType() == ALIAS) || (e.getValue().getType() == DATA_STREAM))
                        .filter(e -> matcher.test(e.getKey()))
                        .map(e -> e.getKey())
                        .toArray(String[]::new);
                if (aliasesAndDataStreamsForPermittedPattern.length > 0) {
                    final String[] resolvedAliasesAndDataStreamIndices = resolver.concreteIndexNames(cs.state(),
                            IndicesOptions.lenientExpandOpen(), includeDataStreams, aliasesAndDataStreamsForPermittedPattern);
                    resolvedIndices.addAll(Arrays.asList(resolvedAliasesAndDataStreamIndices));
                }
            }

            if (Strings.isNotBlank(unresolved)) {
                final String[] resolvedIndicesFromPattern = resolver.concreteIndexNames(cs.state(), IndicesOptions.lenientExpandOpen(), includeDataStreams, unresolved);
                resolvedIndices.addAll(Arrays.asList(resolvedIndicesFromPattern));
            }

            if (appendUnresolved || resolvedIndices.build().isEmpty()) {
                resolvedIndices.add(unresolved);
            }
            return resolvedIndices.build();
        }
}
