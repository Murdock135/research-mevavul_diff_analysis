class ensureSearchEntityLoaded {
public boolean ensureSearchEntityLoaded() {
		if (mySearchEntity == null) {
			Optional<Search> searchOpt = mySearchCacheSvc.fetchByUuid(myUuid);
			if (!searchOpt.isPresent()) {
				return false;
			}

			setSearchEntity(searchOpt.get());

			ourLog.trace("Retrieved search with version {} and total {}", mySearchEntity.getVersion(), mySearchEntity.getTotalCount());

			return true;
		}

		if (mySearchEntity.getSearchType() == SearchTypeEnum.HISTORY) {
			if (mySearchEntity.getTotalCount() == null) {
				calculateHistoryCount();
			}
		}

		return true;
	}
}
