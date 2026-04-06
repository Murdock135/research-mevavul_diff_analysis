class setDataProvider {
private void setDataProvider(int nodesPerLevel, int depth) {
        grid.setDataProvider(
                new LazyHierarchicalDataProvider(nodesPerLevel, depth) {
                    @Override
                    protected Stream<HierarchicalTestBean> fetchChildrenFromBackEnd(
                            HierarchicalQuery<HierarchicalTestBean, Void> query) {
                        VaadinRequest currentRequest = VaadinService
                                .getCurrentRequest();
                        if (!currentRequest.equals(lastRequest)) {
                            requestCount++;
                        }
                        lastRequest = currentRequest;
                        requestCountField
                                .setValue(String.valueOf(requestCount));

                        fetchCount++;
                        fetchCountField.setValue(String.valueOf(fetchCount));

                        return super.fetchChildrenFromBackEnd(query);
                    }

                    @Override
                    public Object getId(HierarchicalTestBean item) {
                        return item != null ? item.toString() : "null";
                    }
                });
    }
}
