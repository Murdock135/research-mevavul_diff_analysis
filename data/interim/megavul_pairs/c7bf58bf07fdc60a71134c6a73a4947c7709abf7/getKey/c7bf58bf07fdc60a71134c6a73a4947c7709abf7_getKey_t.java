class getKey {
private byte[] getKey(K key) {
        return stringSerializer.serialize(CommonUtils.joinString(CACHE_PREFIX, region, Constants.DOT, key));
    }
}
