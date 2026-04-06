class collectChanges {
public void collectChanges(Consumer<NodeChange> collector) {
        boolean isAttached = isAttached();
        if (isAttached != wasAttached) {
            if (isAttached) {
                collector.accept(new NodeAttachChange(this));

                // Make all changes show up as if the node was recently attached
                clearChanges();
                forEachFeature(NodeFeature::generateChangesFromEmpty);
            } else {
                collector.accept(new NodeDetachChange(this));
            }
            wasAttached = isAttached;
        }

        if (!isAttached()) {
            return;
        }

        if (isInitialChanges && !isVisible()) {
            if (hasFeature(ElementData.class)) {
                doCollectChanges(collector,
                        Stream.of(getFeature(ElementData.class)));
            }
            return;
        }

        if (isInactive()) {
            if (isInitialChanges) {
                // send only required (reported) features updates
                Stream<NodeFeature> initialFeatures = Stream
                        .concat(featureSet.mappings.keySet().stream()
                                .filter(this::isReportedFeature)
                                .map(this::getFeature), getDisallowFeatures());
                doCollectChanges(collector, initialFeatures);
            } else {
                doCollectChanges(collector, getDisallowFeatures());
            }
        } else {
            doCollectChanges(collector, getInitializedFeatures());
        }
    }
}
