class onRequestRows {
protected void onRequestRows(int firstRowIndex, int numberOfRows,
            int firstCachedRowIndex, int cacheSize) {
        if (numberOfRows > getMaximumAllowedRows()) {
            throw new IllegalStateException(
                    "Client tried fetch more rows than allowed. This is denied to prevent denial of service.");
        }
        setPushRows(Range.withLength(firstRowIndex, numberOfRows));
        markAsDirty();
    }
}
