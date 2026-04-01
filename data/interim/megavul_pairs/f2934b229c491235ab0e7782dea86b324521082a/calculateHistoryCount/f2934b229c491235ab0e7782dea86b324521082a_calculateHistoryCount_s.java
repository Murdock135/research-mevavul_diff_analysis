class calculateHistoryCount {
private void calculateHistoryCount() {
		myMemoryCacheService.get(MemoryCacheService.CacheEnum.HISTORY_COUNT, key, supplier);

		new TransactionTemplate(myTxManager).executeWithoutResult(t->{
			HistoryBuilder historyBuilder = myHistoryBuilderFactory.newHistoryBuilder(mySearchEntity.getResourceType(), mySearchEntity.getResourceId(), mySearchEntity.getLastUpdatedLow(), mySearchEntity.getLastUpdatedHigh());
			Long count = historyBuilder.fetchCount(getRequestPartitionId());
			mySearchEntity.setTotalCount(count.intValue());
		});
	}
}
