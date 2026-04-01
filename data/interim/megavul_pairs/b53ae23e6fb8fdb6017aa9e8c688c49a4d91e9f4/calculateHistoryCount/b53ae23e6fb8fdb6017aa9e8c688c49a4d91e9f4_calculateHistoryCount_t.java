class calculateHistoryCount {
private void calculateHistoryCount() {
		MemoryCacheService.HistoryCountKey key;
		if (mySearchEntity.getResourceId() != null) {
			key = MemoryCacheService.HistoryCountKey.forInstance(mySearchEntity.getResourceId());
		} else if (mySearchEntity.getResourceType() != null) {
			key = MemoryCacheService.HistoryCountKey.forType(mySearchEntity.getResourceType());
		} else {
			key = MemoryCacheService.HistoryCountKey.forSystem();
		}

		Function<MemoryCacheService.HistoryCountKey, Integer> supplier = k -> new TransactionTemplate(myTxManager).execute(t -> {
			HistoryBuilder historyBuilder = myHistoryBuilderFactory.newHistoryBuilder(mySearchEntity.getResourceType(), mySearchEntity.getResourceId(), mySearchEntity.getLastUpdatedLow(), mySearchEntity.getLastUpdatedHigh());
			Long count = historyBuilder.fetchCount(getRequestPartitionId());
			return count.intValue();
		});

		boolean haveOffset = mySearchEntity.getLastUpdatedLow() != null || mySearchEntity.getLastUpdatedHigh() != null;

		switch (myDaoConfig.getHistoryCountMode()) {
			case COUNT_ACCURATE: {
				int count = supplier.apply(key);
				mySearchEntity.setTotalCount(count);
				break;
			}
			case CACHED_ONLY_WITHOUT_OFFSET: {
				if (!haveOffset) {
					int count = myMemoryCacheService.get(MemoryCacheService.CacheEnum.HISTORY_COUNT, key, supplier);
					mySearchEntity.setTotalCount(count);
				}
				break;
			}
			case COUNT_DISABLED: {
				break;
			}
		}

	}
}
