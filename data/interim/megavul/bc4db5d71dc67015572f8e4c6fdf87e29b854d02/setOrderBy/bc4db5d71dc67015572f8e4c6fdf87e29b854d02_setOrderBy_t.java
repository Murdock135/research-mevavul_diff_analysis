class setOrderBy {
public void setOrderBy(String orderBy) {
		orderBy = SQLUtil.sanitizeSortBy(orderBy);
		this.orderBy = orderBy;
	}
}
