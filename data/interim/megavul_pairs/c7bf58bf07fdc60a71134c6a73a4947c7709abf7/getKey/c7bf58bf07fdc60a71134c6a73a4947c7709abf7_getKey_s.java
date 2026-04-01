class getKey {
private byte[] getKey(K key) {
        return stringSerializer.serialize(CommonUtils.joinString(region, Constants.DOT, key));
    }
}
